package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import configuration.JadeAgent;
import model.Customer;
import model.Order;

import java.util.ArrayList;

@JadeAgent(number = 2)
public class CustomerAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Hello! Custom-agent " + getAID().getName() + " is ready.");

        Object[] args = getArguments();
        Integer id;
        if (args != null && args.length > 0) {
            id = (Integer) args[0];
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
                checkMenuMsg.addReceiver(new AID("Manager", AID.ISLOCALNAME));
                checkMenuMsg.setContent(String.valueOf(dish_id));
                send(checkMenuMsg);

                MessageTemplate template = MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM
                        ), MessageTemplate.MatchSender(new AID("manager", AID.ISLOCALNAME))
                );
                ACLMessage message = receive(template);
                System.out.println("The dish will be served in" + message + "minutes\n");
            }
        }
    }
}
