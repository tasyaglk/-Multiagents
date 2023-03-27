package agents;

import agents.models.Customer;
import agents.models.Order;
import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

@JadeAgent(number = 2)
public class CustomerAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Hello! Custom-agent " + CustomerAgent.class.getName() + " is ready.");

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(CustomerAgent.class.getName());
        serviceDescription.setType(CustomerAgent.class.getName()); // died tut
        dfAgentDescription.addServices(serviceDescription);
        addBehaviour(new Answer());

    }


    public class Answer extends CyclicBehaviour {

        @Override
        public void action() {
            //MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive();

//            MessageTemplate templateOrder = MessageTemplate.and(
//                    MessageTemplate.MatchPerformative(ACLMessage.INFORM
//                    ), MessageTemplate.MatchSender(new AID("agents.SupervisorAgent", AID.ISLOCALNAME))
//            );
//            ACLMessage msg = receive(templateOrder);
            Integer id;
            if (msg != null) {
                id = Integer.valueOf(msg.getContent());
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
                    checkMenuMsg.addReceiver(new AID("agents.SupervisorAgent", AID.ISLOCALNAME));
                    checkMenuMsg.setContent(String.valueOf(dish_id));
                    send(checkMenuMsg);

                    MessageTemplate template = MessageTemplate.and(
                            MessageTemplate.MatchPerformative(ACLMessage.INFORM
                            ), MessageTemplate.MatchSender(new AID("SupervisorAgent", AID.ISLOCALNAME))
                    );
                    ACLMessage message = receive(template);
                    System.out.println("The dish will be served in" + message + "minutes\n");
                }
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