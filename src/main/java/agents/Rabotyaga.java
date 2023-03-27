package agents;

import agents.models.*;
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
import service.CreateLogService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;

@JadeAgent
public class Rabotyaga extends Agent {
    AID[] receivers;
    CreateLogService logService;
    @Override
    protected void setup() {
        System.out.println("Hello! rabotyaga-agent " + getAID().getName() + " is ready.");
        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(Rabotyaga.class.getName());
        serviceDescription.setType(Rabotyaga.class.getName());
        dfAgentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        final ACLMessage[] message = new ACLMessage[1];
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                message[0] = myAgent.receive();
                //logService.addLog("Rabotyaga receives message :" + message[0].getContent());
            }

            @Override
            public boolean done() {
                return false;
            }
        });
        if (message[0] != null) {
            Integer dishCard = parseInt(message[0].getContent());
            boolean haveIngredients = false;
            List<Equipment> equipment = EquipmentAll.getEquipment();
            Float time;
            List<Dish> dishCards = DishCards.getDishCards();
            StringBuilder timeForCook = new StringBuilder();
            for (Dish dish : dishCards) {
                if (haveIngredients) {
                    break;
                }
                if (Objects.equals(dish.getCardId(), dishCard)) {
                    List<Operations> mas = dish.getOperations();
                    for (Operations operProd : mas) {
                        Integer eq = operProd.getEquipType();

                        List<QuantityOperations> operProducts = operProd.getOperProducts();
                        ArrayList<Products> products = Stock.getProducts();
                        for (QuantityOperations prodTypes : operProducts) {
                            for (Products prod : products) {
                                if (Objects.equals(prod.getProdItemType(), prodTypes.getProdType())) {
                                    if (prod.getProdItemQuantity() < prodTypes.getProdQuantity()) {
                                        addBehaviour(new TickerBehaviour(this, 10000) {
                                            @Override
                                            protected void onTick() {
                                                // Update the list of seller agents
                                                DFAgentDescription template = new DFAgentDescription();
                                                ServiceDescription sd = new ServiceDescription();
                                                sd.setType(MenuAgent.class.getName());
                                                template.addServices(sd);
                                                try {
                                                    DFAgentDescription[] result = DFService.search(myAgent, template);
                                                    receivers = new AID[result.length];
                                                    for (int i = 0; i < result.length; ++i) {
                                                        receivers[i] = result[i].getName();
                                                    }
                                                } catch (FIPAException fe) {
                                                    fe.printStackTrace();
                                                }
                                                myAgent.addBehaviour(new SendMessage("NO", receivers));
                                                //logService.addLog("CookAgent sends message NO");
                                            }
                                        });
                                        haveIngredients = true;
                                        break;
                                    }
                                }
                            }
                            if (haveIngredients) {
                                break;
                            }
                        }
                        if (haveIngredients) {
                            break;
                        }
                        time = dish.getCardTime();
                        for (QuantityOperations prodType : operProducts) {
                            for (Products prod : products) {
                                if (Objects.equals(prod.getProdItemType(), prodType.getProdType())) {
                                    prod.changeProdItemQuantity(prodType.getProdQuantity());
                                }
                            }
                        }
                        for (Equipment oneEquipment : equipment) {
                            if (Objects.equals(oneEquipment.getEquipType(), eq))
                            {
                                addBehaviour(new TickerBehaviour(this, 10000) {
                                    @Override
                                    protected void onTick() {
                                        // Update the list of seller agents
                                        DFAgentDescription template = new DFAgentDescription();
                                        ServiceDescription sd = new ServiceDescription();
                                        sd.setType(EquipAgent.class.getName());
                                        template.addServices(sd);
                                        try {
                                            DFAgentDescription[] result = DFService.search(myAgent, template);
                                            receivers = new AID[result.length];
                                            for (int i = 0; i < result.length; ++i) {
                                                receivers[i] = result[i].getName();
                                            }
                                        } catch (FIPAException fe) {
                                            fe.printStackTrace();
                                        }
                                        myAgent.addBehaviour(new SendMessage(oneEquipment.getEquipType().toString(), receivers));
                                        //logService.addLog("CookAgent sends message: " + oneEquipment.getEquipType());
                                    }
                                });
                                final ACLMessage[] ans = new ACLMessage[1];
                                addBehaviour(new Behaviour() {
                                    @Override
                                    public void action() {
                                        ans[0] = myAgent.receive();
                                        //logService.addLog("Rabotyaga receives message :" + ans[0].getContent());
                                    }

                                    @Override
                                    public boolean done() {
                                        return false;
                                    }
                                });
                                // equip type

                                // eqID + ' ' + idCook + time
                                timeForCook.append(ans[0].getContent()).append(' ').append(time);
                            }
                        }

                    }
                }
            }

            addBehaviour(new TickerBehaviour(this, 10000) {
                @Override
                protected void onTick() {
                    // Update the list of seller agents
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType(MenuAgent.class.getName());
                    template.addServices(sd);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        receivers = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
                            receivers[i] = result[i].getName();
                        }
                    } catch (FIPAException fe) {
                        fe.printStackTrace();
                    }
                    myAgent.addBehaviour(new SendMessage(String.valueOf(timeForCook), receivers));
                    //logService.addLog("CookAgent sends message: " + timeForCook);
                }
            });
        }
    }
}
