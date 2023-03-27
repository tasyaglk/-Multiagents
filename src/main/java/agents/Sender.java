package agents;

import behaviour.SendMessage;
import configuration.JadeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

@JadeAgent
public class Sender extends Agent {
    AID[] receivers;
    @Override
    protected void setup() {
        System.out.println("Hello! Sender " + getAID().getName() + " na otpravke");

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(Sender.class.getName());
        serviceDescription.setType(Sender.class.getName()); // died tut
        dfAgentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new TickerBehaviour(this, 10000) {
            @Override
            protected void onTick() {
                // Update the list of seller agents
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType(Sender.class.getName());
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
                myAgent.addBehaviour(new SendMessage("Мы очень-очень старались, но у нас не получилось реализовать отправку сообщений =((", receivers));
            }
        });
    }
}