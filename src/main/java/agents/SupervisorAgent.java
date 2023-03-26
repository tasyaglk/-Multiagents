package agents;

import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

@JadeAgent
public class SupervisorAgent extends Agent {
    private static AgentContainer container;
    private AgentController orderAgent;
    public static final String AGENT_TYPE = "manager";
    public static final String AGENT_NAME = "Manager-agent";

    @Override
    protected void setup() {
        System.out.println("Supervisor Agent " + getAID().getName() + " is ready.");
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(AGENT_TYPE);
        serviceDescription.setName(AGENT_NAME);
        agentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        addBehaviour(new CreateOrder());
    }

    public class CreateOrder extends OneShotBehaviour {
        public void action() {
            container = getContainerController();
            MessageTemplate templateOrder = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM
                    ), MessageTemplate.MatchSender(new AID("customer", AID.ISLOCALNAME))
            );
            ACLMessage msg = myAgent.receive(templateOrder); // spisok zakazov
            if (msg != null) {
                try {
                    String[] order = msg.getContent().split(" ");
                    for (String s : order) {
                        ACLMessage ansTime = new ACLMessage((ACLMessage.INFORM));
                        ansTime.addReceiver(new AID("order", AID.ISLOCALNAME));
                        ansTime.setContent(s);
                        send(ansTime);

                        MessageTemplate template = MessageTemplate.and(
                                MessageTemplate.MatchPerformative(ACLMessage.INFORM
                                ), MessageTemplate.MatchSender(new AID("order", AID.ISLOCALNAME))
                        );
                        ACLMessage message = receive(template);
                        System.out.println(message.getContent());

                        destroyAgent(message.getSender());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                block();
                destroyAgent(getAID("menu"));
                destroyAgent(getAID("equipment"));
                destroyAgent(getAID("cook"));
                destroyAgent(getAID("rabotyaga"));
                doDelete();
            }
        }
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Printout a dismissal message    System.out.println("Seller-agent " + getAID().getName() + " terminating.");
    }

    private void destroyAgent(AID agent) {
        try {
            container.getAgent(agent.getLocalName()).kill();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

}

