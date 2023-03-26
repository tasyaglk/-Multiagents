package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import configuration.JadeAgent;
import model.Equipment;
import model.EquipmentAll;

import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;

@JadeAgent(number = 2)
public class EquipAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Hello! Equipment-agent " + getAID().getName() + " is ready.");

        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST
                ), MessageTemplate.MatchSender(new AID("rabotyaga", AID.ISLOCALNAME))
        );
        ACLMessage msg = receive(template); // equip type
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
                    checkCookMsg.addReceiver(new AID("cook", AID.ISLOCALNAME));
                    checkCookMsg.setContent(eqmnt.getEquipType().toString());
                    send(checkCookMsg);

                    MessageTemplate template2 = MessageTemplate.and(
                            MessageTemplate.MatchPerformative(ACLMessage.INFORM
                            ), MessageTemplate.MatchSender(new AID("cook", AID.ISLOCALNAME))
                    );
                    ACLMessage message = receive(template2);

                    eqmnt.changeEquipActivity();

                    ACLMessage ansOrderMsg = new ACLMessage((ACLMessage.INFORM));
                    ansOrderMsg.addReceiver(msg.getSender());
                    // eqID + ' ' + idcook
                    ansOrderMsg.setContent(equipID.toString() + ' ' + message.getContent());
                    send(ansOrderMsg);
                }
            }
        }
}