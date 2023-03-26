package agents;

import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import lombok.SneakyThrows;
import model.Cook;
import model.CookersAll;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Thread.sleep;

@JadeAgent(number = 3)
public class CookAgent extends Agent {

    ArrayList<Cook> infoCook = CookersAll.getCookers();

    @SneakyThrows
    @Override
    protected void setup() {
        System.out.println("Hello! Cook-agent " + getAID().getName() + " is ready.");
        ACLMessage msg = receive(); // spisok zakazov
        Integer cookID = null;

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();

        serviceDescription.setType(CookAgent.class.getName());
        dfAgentDescription.addServices(serviceDescription);

        DFService.register(this, dfAgentDescription);

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