package behaviour;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

import java.util.Objects;

public class CreateOrder extends OneShotBehaviour {

    public void action() {
        // Wait for an order to be received
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            try {
                // Retrieve the order details from the message content
                String order = msg.getContent();
                ACLMessage checkMenuMsg = new ACLMessage(ACLMessage.REQUEST);
                checkMenuMsg.addReceiver(new AID("menu", AID.ISLOCALNAME));
                checkMenuMsg.setContentObject(order);
                myAgent.send(checkMenuMsg);

                // Wait for a response from the kitchen
                ACLMessage reply = myAgent.receive();
                if (reply != null) {
                    // Process the reply
                    if (!Objects.equals(reply.getContent(), "NO")) {
                        System.out.println("Order confirmed! Time: " + reply);
                    } else {
                        System.out.println("Error processing order.");
                    }
                } else {
                    // No reply received
                    // вообще мы чекаем меню которое чекает у кухни
                    System.out.println("Timeout: no response received from kitchen.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // No message received
            block();
        }
    }
}

