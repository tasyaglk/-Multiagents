package agents;

import agents.models.Customer;
import agents.models.Order;
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

@JadeAgent(number = 2)
public class CustomerAgent extends Agent {

    private AID[] testAgents;
    CreateLogService logService;

    @Override
    protected void setup() {
        System.out.println("Hello! Custom-agent " + CustomerAgent.class.getName() + " is ready.");

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(CustomerAgent.class.getName());
        serviceDescription.setType(CustomerAgent.class.getName()); // died tut
        dfAgentDescription.addServices(serviceDescription);
        final ACLMessage[] msg = new ACLMessage[1];
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                msg[0] = myAgent.receive();
                if (msg[0] != null) {
                    //logService.addLog("CustomerAgent receives message :" + msg[0].getContent());
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });
        int id;
        if (msg[0] != null) {
            id = Integer.parseInt(msg[0].getContent());
            ArrayList<Integer> allOrders = null;
            ArrayList<Customer> infoCustomer = Order.getVisitorsOrders();
            int i = -1;
            for (Customer oneCustomer : infoCustomer) {
                i++;
                if (i == id) {
                    allOrders = oneCustomer.getVisOrdDishes();
                }
            }

            assert allOrders != null;
            for (Integer dish_id : allOrders) {
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
                            testAgents = new AID[result.length];
                            for (int i = 0; i < result.length; ++i) {
                                testAgents[i] = result[i].getName();
                            }
                        } catch (FIPAException fe) {
                            fe.printStackTrace();
                        }
                        myAgent.addBehaviour(new SendMessage(String.valueOf(dish_id), testAgents));
                        //logService.addLog("CustomerAgent receives message :" + dish_id);
                    }
                });

                addBehaviour(new Behaviour() {
                    @Override
                    public void action() {
                        ACLMessage message = myAgent.receive();
                        if (message != null) {
                            System.out.println("The dish will be served in" + message.getContent() + "minutes\n");
                        }
                    }

                    @Override
                    public boolean done() {
                        return false;
                    }
                });

            }
        }

    }


}
