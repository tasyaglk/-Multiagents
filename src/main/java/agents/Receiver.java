package agents;

import configuration.JadeAgent;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
@JadeAgent
public class Receiver extends Agent {

    @Override
    protected void setup() {
        System.out.println("Hello! Receiver " + getAID().getName() + " na baze");

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(Receiver.class.getName());
        serviceDescription.setType(Receiver.class.getName()); // died tut
        dfAgentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage message = myAgent.receive();
                if(message != null)
                {
                    System.out.println(message.getContent());
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });
    }
}