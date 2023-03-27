package behaviour;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import util.JsonMessage;

public class SendMessage extends Behaviour {

    private final String message;
    private final AID[] recipients;

    public SendMessage(String message, AID[] recipients) {
        this.message = message;
        this.recipients = recipients;
    }

    @Override
    public void action() {
        JsonMessage cfp = new JsonMessage(ACLMessage.CFP);
        for (AID recipient : recipients) {
            cfp.addReceiver(recipient);
        }
        cfp.setContent(message);
        myAgent.send(cfp);
    }

    @Override
    public boolean done() {
        return true;
    }
}