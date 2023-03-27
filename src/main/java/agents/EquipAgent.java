package agents;

import agents.models.Equipment;
import agents.models.EquipmentAll;
import behaviour.SendMessage;
import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;

@JadeAgent(number = 2)
public class EquipAgent extends Agent {
    AID[] recievers;

    @SneakyThrows
    @Override
    protected void setup() {
        System.out.println("Hello! Equipment-agent " + getAID().getName() + " is ready.");

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(CookAgent.class.getName());
        serviceDescription.setType(EquipAgent.class.getName());
        dfAgentDescription.addServices(serviceDescription);

        DFService.register(this, dfAgentDescription);
        final ACLMessage[] msg = {null};
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                msg[0] = myAgent.receive();
            }

            @Override
            public boolean done() {
                return false;
            }
        });
        if (msg[0] != null) {
            Integer eqTp = parseInt(msg[0].getContent());
            List<Equipment> equipment = EquipmentAll.getEquipment();
            Integer equipID;
            for (Equipment eqmnt : equipment) {
                if (Objects.equals(eqmnt.getEquipType(), eqTp)) {
                    boolean ready = eqmnt.getEquipActive();
                    while (!ready)
                        try {
                            sleep(2000);
                            ready = eqmnt.getEquipActive();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                }
                eqmnt.changeEquipActivity();
                equipID = eqmnt.getEquipId();

                addBehaviour(new TickerBehaviour(this, 10000) {
                    @Override
                    protected void onTick() {
                        // Update the list of seller agents
                        DFAgentDescription template = new DFAgentDescription();
                        ServiceDescription sd = new ServiceDescription();
                        sd.setType(CookAgent.class.getName());
                        template.addServices(sd);
                        try {
                            DFAgentDescription[] result = DFService.search(myAgent, template);
                            recievers = new AID[result.length];
                            for (int i = 0; i < result.length; ++i) {
                                recievers[i] = result[i].getName();
                            }
                        } catch (FIPAException fe) {
                            fe.printStackTrace();
                        }
                        myAgent.addBehaviour(new SendMessage(eqmnt.getEquipType().toString(), recievers));
                    }
                });


                final ACLMessage[] message = new ACLMessage[1];
                addBehaviour(new Behaviour() {
                    @Override
                    public void action() {
                        message[0] = myAgent.receive();
                    }

                    @Override
                    public boolean done() {
                        return false;
                    }
                });

                eqmnt.changeEquipActivity();

                ACLMessage ansOrderMsg = new ACLMessage((ACLMessage.INFORM));
                ansOrderMsg.addReceiver(msg[0].getSender());
                // eqID + ' ' + idcook
                ansOrderMsg.setContent(equipID.toString() + ' ' + message[0].getContent());
                send(ansOrderMsg);
            }
        }
    }
}