package agents;

import agents.models.Cook;
import agents.models.CookersAll;
import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Thread.sleep;

@JadeAgent(number = 3)
public class CookAgent extends Agent {

    ArrayList<Cook> infoCook = CookersAll.getCookers();

    @Override
    protected void setup() {
        System.out.println("Hello! СЃook-agent " + getAID().getName() + " is ready.");
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST
                ), MessageTemplate.MatchSender(new AID("EquipAgent1", AID.ISLOCALNAME))
        );
        ACLMessage msg = receive(template); // spisok zakazov
        System.out.println("Cook receives message : ");
        System.out.println(msg);
        Integer cookID = null;

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(CookAgent.class.getName());
        serviceDescription.setType(CookAgent.class.getName()); // died tut
        dfAgentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }

        if (msg != null) {
            boolean isEquipmentFound = false;
            while(!isEquipmentFound) {
                makeSleep(2000);
                for (Cook cook : infoCook) {
                    if (cook.getCookActive()) {
                        cook.changeCookActive();
                        cookID = cook.getCookId();
                        isEquipmentFound = true;
                        break;
                    }
                }
            }
            makeSleep(6969);
            for(Cook cook : infoCook) {
                if(Objects.equals(cook.getCookId(), cookID)) {
                    cook.changeCookActive();
                    break;
                }
            }
            AID receiverAID = msg.getSender();
            ACLMessage checkMenuMsg = new ACLMessage((ACLMessage.INFORM));
            checkMenuMsg.addReceiver(receiverAID);
            checkMenuMsg.setContent(cookID.toString());
            send(checkMenuMsg);
            System.out.println("Cook sends message : ");
            System.out.println(checkMenuMsg);
        }
    }

    private void makeSleep(int time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}