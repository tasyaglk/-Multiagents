package agents;

import agents.models.Menu;
import agents.models.MenuDishes;
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

import java.util.List;
import java.util.Objects;

@JadeAgent
public class MenuAgent extends Agent {
    AID[] receivers;

    CreateLogService logService;
    @Override
    protected void setup() {
        // poluchaem dishid
        System.out.println("Hello! Menu-agent " + getAID().getName() + " is ready.");
        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(MenuAgent.class.getName());
        serviceDescription.setType(MenuAgent.class.getName());
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
                //logService.addLog("MenuAgent receives message :" + message[0].getContent());
            }

            @Override
            public boolean done() {
                return false;
            }
        });
        if (message[0] != null) {
            String dishId = message[0].getContent();

            List<Menu> dishesOfMenu = MenuDishes.getMenuDishes();
            for (Menu mn : dishesOfMenu) {
                if (mn.getMenuDishId().toString().equals(dishId)) {

                    addBehaviour(new TickerBehaviour(this, 10000) {
                        @Override
                        protected void onTick() {
                            // Update the list of seller agents
                            DFAgentDescription template = new DFAgentDescription();
                            ServiceDescription sd = new ServiceDescription();
                            sd.setType(Rabotyaga.class.getName());
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
                            myAgent.addBehaviour(new SendMessage(mn.getMenuDishCard().toString(), receivers));
                            //logService.addLog("MenuAgent sends message : " + mn.getMenuDishCard());
                        }
                    });
                }
            }
            final ACLMessage[] message2 = new ACLMessage[1];
            addBehaviour(new Behaviour() {
                @Override
                public void action() {
                    message2[0] = myAgent.receive();
                    //logService.addLog("MenuAgent receives message :" + message2[0].getContent());
                }

                @Override
                public boolean done() {
                    return false;
                }
            }); // eqID + ' ' + idCook + time
            if (message2[0] != null && !Objects.equals(message[0].getContent(), "NO")) {
                addBehaviour(new TickerBehaviour(this, 10000) {
                    @Override
                    protected void onTick() {
                        // Update the list of seller agents
                        DFAgentDescription template = new DFAgentDescription();
                        ServiceDescription sd = new ServiceDescription();
                        sd.setType(OrderAgent.class.getName());
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
                        myAgent.addBehaviour(new SendMessage(message2[0].getContent(), receivers));
                        //logService.addLog("MenuAgent sends message : " + message2[0].getContent());
                    }
                });
            }
        }
    }
}