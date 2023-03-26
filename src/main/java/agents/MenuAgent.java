package agents;

import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Menu;
import model.MenuDishes;

import java.util.List;
import java.util.Objects;

@JadeAgent
public class MenuAgent extends Agent {
    @Override
    protected void setup() {
// poluchaem dishid
        System.out.println("Hello! Menu-agent " + getAID().getName() + " is ready.");


        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM
                ), MessageTemplate.MatchSender(new AID("order", AID.ISLOCALNAME))
        );
        ACLMessage message = receive(template);
        if (message != null) {
            String dishId = message.getContent();

            List<Menu> mennu = MenuDishes.getMenuDishes();
            for (Menu mn : mennu) {
                if (mn.getMenuDishId().toString().equals(dishId)) {
                    ACLMessage ifCouldCook = new ACLMessage((ACLMessage.REQUEST));
                    ifCouldCook.addReceiver(new AID("rabotyaga", AID.ISLOCALNAME));
                    ifCouldCook.setContent(mn.getMenuDishCard().toString());
                    send(ifCouldCook);
                }
            }

            MessageTemplate template2 = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM
                    ), MessageTemplate.MatchSender(new AID("rabotyaga", AID.ISLOCALNAME))
            );
            ACLMessage message2 = receive(template2); // eqID + ' ' + idcook + time
            if (message2 != null && !Objects.equals(message.getContent(), "NO")) {
                ACLMessage ansTime = new ACLMessage((ACLMessage.INFORM));
                ansTime.addReceiver(message.getSender());
                ansTime.setContent(message2.getContent());
                send(ansTime);
            }
        }
    }
}