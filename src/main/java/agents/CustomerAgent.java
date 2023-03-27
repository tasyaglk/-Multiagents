package agents;

import agents.models.Customer;
import agents.models.Order;
import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

@JadeAgent(number = 2)
public class CustomerAgent extends Agent {
    ACLMessage msg;

    @Override
    protected void setup() {
        System.out.println("Hello! Custom-agent " + getAID().getName() + " is ready.");

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(CustomerAgent.class.getName());
        serviceDescription.setType(CustomerAgent.class.getName()); // died tut
        dfAgentDescription.addServices(serviceDescription);

        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();

                serviceDescription.setType(SupervisorAgent.AGENT_TYPE);
                template.addServices(serviceDescription);
                MessageTemplate templateOrder = MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM
                        ), MessageTemplate.MatchSender(new AID(SupervisorAgent.class.getName(), AID.ISLOCALNAME))
                );
                msg = receive(templateOrder);
                System.out.println(myAgent.getName());
                System.out.println("Customer receives message : ");
                System.out.println(msg);
                if (msg != null) {
                    addBehaviour(new Answer());
                }
            }
        });
    }


    public class Answer extends CyclicBehaviour {

        @Override
        public void action() {
            int id;
            id = Integer.parseInt(msg.getContent());
            ArrayList<Integer> allorders = null;
            ArrayList<Customer> infoCustomer = Order.getVisitorsOrders();
            int i = -1;
            for(Customer cust : infoCustomer) {
                i++;
                if(i == id) {
                    allorders = cust.getVisOrdDishes();
                }
            }

            assert allorders != null;
            for (Integer dish_id : allorders) {
                ACLMessage checkMenuMsg = new ACLMessage((ACLMessage.REQUEST));
                checkMenuMsg.addReceiver(new AID("SupervisorAgent", AID.ISLOCALNAME));
                checkMenuMsg.setContent(String.valueOf(dish_id));
                send(checkMenuMsg);
                System.out.println("Customer sends message : ");
                System.out.println(checkMenuMsg);


                MessageTemplate template = MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM
                        ), MessageTemplate.MatchSender(new AID("SupervisorAgent", AID.ISLOCALNAME))
                );
                ACLMessage message = receive(template);
                System.out.println("The dish will be served in" + message + "minutes\n");
            }
        }

//        protected void takeDown() {
//            try {
//                DFService.deregister(this);
//            } catch (FIPAException fe) {
//                fe.printStackTrace();
//            }
//            // Printout a dismissal message    System.out.println("Seller-agent " + getAID().getName() + " terminating.");
//        }
    }
}
