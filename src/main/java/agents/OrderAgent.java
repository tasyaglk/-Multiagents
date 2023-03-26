package agents;

import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


@JadeAgent(number = 3)
public class OrderAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Hello! Order-agent " + getAID().getName() + " is ready.");

        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM
                ), MessageTemplate.MatchSender(new AID("manager", AID.ISLOCALNAME))
        );
        ACLMessage message = receive(template);

        String dishId;
        if (message != null) {
            dishId = message.getContent();
            ACLMessage checkMenuMsg = new ACLMessage((ACLMessage.REQUEST));
            checkMenuMsg.addReceiver(new AID("menu", AID.ISLOCALNAME));
            checkMenuMsg.setContent(dishId);
            send(checkMenuMsg);

            MessageTemplate templateNext = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM
                    ), MessageTemplate.MatchSender(new AID("menu", AID.ISLOCALNAME))
            );
            ACLMessage messageNext = receive(templateNext);
            if (messageNext != null) {

                ACLMessage ansTime = new ACLMessage((ACLMessage.INFORM));
                ansTime.addReceiver(new AID("manager", AID.ISLOCALNAME));
                ansTime.setContent(dishId + ' ' + messageNext.getContent());
                send(ansTime);

            }
        }
    }
}
