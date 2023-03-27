package agents;

import agents.models.Equipment;
import agents.models.EquipmentAll;
import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;

@JadeAgent(number = 2)
public class EquipAgent extends Agent {
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

        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST
                ), MessageTemplate.MatchSender(new AID("rabotyaga", AID.ISLOCALNAME))
        );
        ACLMessage msg = receive(template); // equip type
        System.out.println("Equip receives message : ");
        System.out.println(msg);
        if (msg != null) {
            Integer eqTp = parseInt(msg.getContent());
            List<Equipment> equipment = EquipmentAll.getEquipment();
            Integer equipID;
            for (Equipment eqmnt : equipment) {
                if (Objects.equals(eqmnt.getEquipType(), eqTp))
                {
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
                ACLMessage checkCookMsg = new ACLMessage((ACLMessage.REQUEST));
                checkCookMsg.addReceiver(new AID("CookAgent1", AID.ISLOCALNAME));
                checkCookMsg.setContent(eqmnt.getEquipType().toString());
                send(checkCookMsg);
                System.out.println("Equip sends message : ");
                System.out.println(checkCookMsg);

                MessageTemplate template2 = MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM
                        ), MessageTemplate.MatchSender(new AID("CookAgent1", AID.ISLOCALNAME))
                );
                ACLMessage message = receive(template2);
                System.out.println("Equip receives message : ");
                System.out.println(message);

                eqmnt.changeEquipActivity();

                ACLMessage ansOrderMsg = new ACLMessage((ACLMessage.INFORM));
                ansOrderMsg.addReceiver(msg.getSender());
                // eqID + ' ' + idcook
                ansOrderMsg.setContent(equipID.toString() + ' ' + message.getContent());
                send(ansOrderMsg);
                System.out.println("Equip sends message : ");
                System.out.println(ansOrderMsg);
            }
        }
    }
}