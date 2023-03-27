package agents;

import agents.models.Cook;
import agents.models.CookersAll;
import behaviour.SendMessage;
import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import service.CreateLogService;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Thread.sleep;

@JadeAgent(number = 3)
public class CookAgent extends Agent {
    AID[] receivers;
    ArrayList<Cook> infoCook = CookersAll.getCookers();

    CreateLogService logService;

    @Override
    protected void setup() {
        System.out.println("Hello! Cook-agent " + getAID().getName() + " is ready.");

        final ACLMessage[] msg = new ACLMessage[1]; // spisok zakazov
        Integer cookID = null;

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(CookAgent.class.getName());
        serviceDescription.setType(CookAgent.class.getName()); // died tut
        dfAgentDescription.addServices(serviceDescription);

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                msg[0] = myAgent.receive();
                //logService.addLog("CookAgent receives message :" + msg[0].getContent());
            }

            @Override
            public boolean done() {
                return false;
            }
        });

        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }

        if (msg[0] != null) {
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

            Integer finalCookID = cookID;
            addBehaviour(new TickerBehaviour(this, 10000) {
                @Override
                protected void onTick() {
                    // Update the list of seller agents
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType(EquipAgent.class.getName());
                    template.addServices(sd);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        receivers = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
                            receivers[i] = result[i].getName();
                        }
                    } catch (FIPAException fe) {
                        fe.printStackTrace();
                    }
                    myAgent.addBehaviour(new SendMessage(finalCookID.toString(), receivers));
                    //logService.addLog("CookAgent receives message :" + finalCookID);
                }
            });

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