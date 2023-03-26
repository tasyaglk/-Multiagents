package behaviour;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;
public class GetOrder extends Behaviour {
    @Override
    public void action() {
        ACLMessage visitorMessage = myAgent.receive();

        if (visitorMessage != null) {
            System.out.println("Your order is processed by " + myAgent.getName() + ". And your order - " + visitorMessage.getContent());
            myAgent.addBehaviour(new CreateOrder());
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
