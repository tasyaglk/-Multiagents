package agents;

import behaviour.SendMessage;
import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import lombok.SneakyThrows;
import service.CreateLogService;
import util.AgentUtils;

@JadeAgent
public class SupervisorAgent extends Agent {
    private AID[] testAgents;

    CreateLogService logService;

    private String index;
    @Override
    protected void setup() {
        System.out.println("Supervisor Agent " + SupervisorAgent.class.getName() + " is ready.");
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(SupervisorAgent.class.getName());
        serviceDescription.setName(SupervisorAgent.class.getName());
        agentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        addBehaviour(new TickerBehaviour(this, 10000) {
            @Override
            protected void onTick() {
                // Update the list of seller agents
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType(CustomerAgent.class.getName());
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    testAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        testAgents[i] = result[i].getName();
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
                myAgent.addBehaviour(new SendMessage("0", testAgents));
                //logService.addLog("CookAgent sends message 0");
            }
        });
        addBehaviour(new CreateOrder());
    }

    public class CreateOrder extends CyclicBehaviour {
        @SneakyThrows
        public void action() {
            addBehaviour(new Behaviour() {
                @Override
                public void action() {
                    ACLMessage msg = myAgent.receive();
                    index = msg.getContent();
                    //logService.addLog("SupervisorAgent receives message :" + index);
                }

                @Override
                public boolean done() {
                    return false;
                }
            });
            if (index != null) {
                try {

                    String[] order = index.split(" ");
                    for (String s : order) {

                        addBehaviour(new TickerBehaviour(this.getAgent(), 10000) {
                            @Override
                            protected void onTick() {
                                // Update the list of seller agents
                                DFAgentDescription template = new DFAgentDescription();
                                ServiceDescription sd = new ServiceDescription();
                                sd.setType(OrderAgent.class.getName());
                                template.addServices(sd);
                                try {
                                    DFAgentDescription[] result = DFService.search(myAgent, template);
                                    testAgents = new AID[result.length];
                                    for (int i = 0; i < result.length; ++i) {
                                        testAgents[i] = result[i].getName();
                                    }
                                } catch (FIPAException fe) {
                                    fe.printStackTrace();
                                }
                                myAgent.addBehaviour(new SendMessage(s, testAgents));
                                //logService.addLog("CookAgent sends message : " + s);
                            }
                        });

                        addBehaviour(new Behaviour() {
                            @Override
                            public void action() {
                                ACLMessage message = myAgent.receive();
                                if(message != null)
                                {
                                    System.out.println(message.getContent());
                                    //logService.addLog("SupervisorAgent receives message :" + message);
                                }
                            }
                            @Override
                            public boolean done() {
                                return false;
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                block();
                AgentUtils.destroyAgentsByType(myAgent, "agents.MenuAgents");
                AgentUtils.destroyAgentsByType(myAgent, EquipAgent.class.getName());
                AgentUtils.destroyAgentsByType(myAgent, CookAgent.class.getName());
                AgentUtils.destroyAgentsByType(myAgent, "agents.Rabotyaga");
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
    }
}