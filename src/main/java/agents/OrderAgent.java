package agents;

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


@JadeAgent(number = 3)
public class OrderAgent extends Agent {
    AID[] receivers;
    CreateLogService logService;

    @Override
    protected void setup() {
        System.out.println("Hello! Order-agent " + getAID().getName() + " is ready.");
        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(OrderAgent.class.getName());
        serviceDescription.setType(MenuAgent.class.getName());
        dfAgentDescription.addServices(serviceDescription);

        final ACLMessage[] message = new ACLMessage[1];
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                message[0] = myAgent.receive();
                //logService.addLog("OrderAgent receives message :" + message[0].getContent());
            }

            @Override
            public boolean done() {
                return false;
            }
        });


        String dishId;
        if (message[0] != null) {
            dishId = message[0].getContent();
            addBehaviour(new TickerBehaviour(this, 10000) {
                @Override
                protected void onTick() {
                    // Update the list of seller agents
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType(MenuAgent.class.getName());
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
                    myAgent.addBehaviour(new SendMessage(dishId, receivers));
                    //logService.addLog("OrderAgent sends message : " + dishId);
                }
            });
            final ACLMessage[] messageNext = new ACLMessage[1];
            addBehaviour(new Behaviour() {
                @Override
                public void action() {
                    messageNext[0] = myAgent.receive();
                    //logService.addLog("OrderAgent receives message :" + messageNext[0].getContent());
                }

                @Override
                public boolean done() {
                    return false;
                }
            });
            if (messageNext[0] != null) {
                addBehaviour(new TickerBehaviour(this, 10000) {
                    @Override
                    protected void onTick() {
                        // Update the list of seller agents
                        DFAgentDescription template = new DFAgentDescription();
                        ServiceDescription sd = new ServiceDescription();
                        sd.setType(SupervisorAgent.class.getName());
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
                        myAgent.addBehaviour(new SendMessage(dishId + ' ' + messageNext[0].getContent(), receivers));
                        //logService.addLog("OrderAgent sends message : " + dishId + ' ' + messageNext[0].getContent());
                    }
                });
            }
        }
    }
}
