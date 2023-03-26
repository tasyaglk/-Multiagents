package util;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.wrapper.ContainerController;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public class AgentUtils {

    @SneakyThrows
    public static void destroyAgentsByType(Agent agent, String type) {
        var dfAgentDescription = new DFAgentDescription();

        var service = new ServiceDescription();
        service.setType(type);

        dfAgentDescription.addServices(service);

        DFAgentDescription[] agentDescriptions = DFService.search(agent, dfAgentDescription);

        Arrays.stream(agentDescriptions)
                .forEach(el -> destroyAgent(agent.getContainerController(), el.getName()));
    }

    @SneakyThrows
    public static void destroyAgent(ContainerController controller, AID agentAid) {
        controller.getAgent(agentAid.getLocalName()).kill();
    }
}
