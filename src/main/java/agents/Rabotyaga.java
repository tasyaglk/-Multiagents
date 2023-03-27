package agents;

import agents.models.*;
import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;

@JadeAgent
public class Rabotyaga extends Agent {

    @Override
    protected void setup() {
        System.out.println("Hello! rabotyaga-agent " + getAID().getName() + " is ready.");
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM
                ), MessageTemplate.MatchSender(new AID("MenuAgent", AID.ISLOCALNAME))
        );
        ACLMessage message = receive(template);
        System.out.println("raboytaga receives message : ");
        System.out.println(message);
        if (message != null) {
            Integer dishCard = parseInt(message.getContent());
            int fl = 0;
            List<Equipment> equipment = EquipmentAll.getEquipment();
            Float timme;
            List<Dish> dishCards = DishCards.getDishCards();
            StringBuilder ansass = new StringBuilder();
            for (Dish dish : dishCards) {
                if (fl == 1) {
                    break;
                }
                if (Objects.equals(dish.getCardId(), dishCard)) {
                    List<Operations> mas = dish.getOperations();
                    for (Operations oper_prod : mas) {
                        Integer eq = oper_prod.getEquipType();

                        List<QuantityOperations> qo = oper_prod.getOperProducts();
                        ArrayList<Products> prdcts = Stock.getProducts();
                        for (QuantityOperations podd : qo) {
                            for (Products prod : prdcts) {
                                if (Objects.equals(prod.getProdItemType(), podd.getProdType())) {
                                    if (prod.getProdItemQuantity() < podd.getProdQuantity()) {
                                        ACLMessage checkEquipMsg = new ACLMessage((ACLMessage.INFORM));
                                        checkEquipMsg.addReceiver(new AID("MenuAgent", AID.ISLOCALNAME));
                                        checkEquipMsg.setContent("NO");
                                        send(checkEquipMsg);
                                        fl = 1;
                                        break;
                                    }
                                }
                            }
                            if(fl == 1) {
                                break;
                            }
                        }
                        if(fl == 1) {
                            break;
                        }
                        timme = dish.getCardTime();
                        for(QuantityOperations podd : qo) {
                            for(Products prod : prdcts) {
                                if(Objects.equals(prod.getProdItemType(), podd.getProdType())) {
                                    prod.changeProdItemQuantity(podd.getProdQuantity());
                                }
                            }
                        }
                        for(Equipment eqmnt : equipment) {
                            if(Objects.equals(eqmnt.getEquipType(), eq))
                            {
                                ACLMessage checkEquipMsg = new ACLMessage((ACLMessage.REQUEST));
                                checkEquipMsg.addReceiver(new AID("EquipAgent", AID.ISLOCALNAME));
                                checkEquipMsg.setContent(eqmnt.getEquipType().toString());
                                send(checkEquipMsg);

                                MessageTemplate template2 = MessageTemplate.and(
                                        MessageTemplate.MatchPerformative(ACLMessage.INFORM
                                        ), MessageTemplate.MatchSender(new AID("EquipAgent", AID.ISLOCALNAME))
                                );
                                ACLMessage ans = receive(template2); // equip type

                                // eqID + ' ' + idcook + time
                                ansass.append(ans.getContent()).append(' ').append(timme);
                            }
                        }

                    }
                }
            }

            ACLMessage ansOrderMsg = new ACLMessage((ACLMessage.INFORM));
            ansOrderMsg.addReceiver(message.getSender());
            // eqID + ' ' + idcook + time
            ansOrderMsg.setContent(String.valueOf(ansass));
            send(ansOrderMsg);
        }
    }
}
