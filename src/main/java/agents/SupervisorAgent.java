package agents;

import agents.models.Customer;
import agents.models.Order;
import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import lombok.SneakyThrows;
import service.CreateLogService;
import util.AgentUtils;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

@JadeAgent
public class SupervisorAgent extends Agent {
    private static AgentContainer container;
    public static final String AGENT_TYPE = "manager";
    public static final String AGENT_NAME = "Manager-agent";

    @Override
    protected void setup() {
//        try {
//            wait(2000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        System.out.println("Supervisor Agent " + SupervisorAgent.class.getName() + " is ready.");
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(CustomerAgent.class.getName());
        serviceDescription.setName(CustomerAgent.class.getName());
        agentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        addBehaviour(new CreateOrder());
    }

    public class CreateOrder extends CyclicBehaviour {
        @SneakyThrows
        public void action() {
            container = getContainerController();
            ACLMessage iD = new ACLMessage((ACLMessage.INFORM));
            iD.addReceiver(new AID(CustomerAgent.class.getName(), AID.ISLOCALNAME));
            iD.setContent("1");
            send(iD);
            System.out.println("Supervisor sends message : ");
            System.out.println(iD);
            CreateLogService.addLog("Supervisor sends message : " + iD);
            MessageTemplate templateOrder = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM
                    ), MessageTemplate.MatchSender(new AID(CustomerAgent.class.getName(), AID.ISLOCALNAME))
            );
            ACLMessage msg = myAgent.receive(templateOrder); // spisok zakazov
            System.out.println("Supervisor receives message : ");
            System.out.println(msg);
            CreateLogService.addLog("Supervisor receives message : " + msg);
            while (msg == null) {
                templateOrder = MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM
                        ), MessageTemplate.MatchSender(new AID(CustomerAgent.class.getName(), AID.ISLOCALNAME))
                );
                msg = myAgent.receive(templateOrder); // spisok zakazov
                System.out.println("Supervisor receives message if first message is null: ");
                System.out.println(msg);
                sleep(1000);
            }
            if (msg != null) {
                try {
                    System.out.println((msg.getContent()));

                    String[] order = msg.getContent().split(" ");
                    for (String s : order) {
                        ACLMessage ansTime = new ACLMessage((ACLMessage.INFORM));
                        ansTime.addReceiver(new AID("OrderAgent", AID.ISLOCALNAME));
                        ansTime.setContent(s);
                        send(ansTime);
                        System.out.println("Supervisor sends message : ");
                        System.out.println(ansTime);

                        MessageTemplate template = MessageTemplate.and(
                                MessageTemplate.MatchPerformative(ACLMessage.INFORM
                                ), MessageTemplate.MatchSender(new AID("OrderAgent", AID.ISLOCALNAME))
                        );
                        ACLMessage message = receive(template);
                        System.out.println("Supervisor receives message : ");
                        System.out.println(message);

                        //destroyAgent(message.getSender());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                block();
                AgentUtils.destroyAgentsByType(myAgent, "menu");
                AgentUtils.destroyAgentsByType(myAgent, EquipAgent.class.getName());
                AgentUtils.destroyAgentsByType(myAgent, CookAgent.class.getName());
                AgentUtils.destroyAgentsByType(myAgent, "rabotyaga");
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

