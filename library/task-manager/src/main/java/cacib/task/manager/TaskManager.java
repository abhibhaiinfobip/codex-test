package cacib.task.manager;

import cacib.shared.interfaces.task.ConfigurableTaskStep;
import cacib.shared.interfaces.task.TaskFlowDefinition;
import cacib.shared.interfaces.task.TaskStep;
import org.pf4j.PluginWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskManager {

    private final List<TaskFlowDefinition> flows;
    private final CustomPluginManager pluginManager;

    public TaskManager(List<TaskFlowDefinition> flows, CustomPluginManager pluginManager) {
        this.flows = Objects.requireNonNull(flows, "flows must not be null");
        this.pluginManager = Objects.requireNonNull(pluginManager, "pluginManager must not be null");
    }

    public void execute(Map<String, Object> input) {
        for (TaskFlowDefinition flow : flows) {
            System.out.println("=== Starting flow: " + flow.getFlowId() + " ===");
            runSteps(flow.getSteps(), input);
        }
    }

    public Map<String, Object> executeFlow(String flowId, Map<String, Object> input) {
        TaskFlowDefinition flow = flows.stream()
                .filter(f -> f.getFlowId().equals(flowId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown flow: " + flowId));
        System.out.println("=== Starting flow: " + flow.getFlowId() + " ===");
        // Validate input
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input map must not be null or empty");
        }
        // Ensure all steps are valid
        List<TaskStep> invalidSteps = flow.getSteps().stream()
                .filter(step -> !(step instanceof ConfigurableTaskStep))
                .toList();
        if (!invalidSteps.isEmpty()) {
            throw new IllegalArgumentException("Flow " + flowId + " contains invalid steps: " +
                    invalidSteps.stream().map(TaskStep::getClass).map(Class::getSimpleName).collect(Collectors.joining(", ")));
        }
        // Ensure all steps have unique IDs
        List<String> stepIds = flow.getSteps().stream()
                .map(TaskStep::getId)
                .filter(Objects::nonNull)
                .toList();
        //Print outline of the flow with arrows and emoticons
        if (stepIds.isEmpty()) {
            throw new IllegalArgumentException("Flow " + flowId + " has no steps defined");
        }
        //Print outline of the flow with arrows and emoticons
        System.out.println("Flow outline: " + stepIds.stream()
                .map(id -> "→ " + id + "👉👉")
                .collect(Collectors.joining(" ")));
        // Build a fresh mutable context
        Map<String, Object> ctx = new HashMap<>(input);
        runSteps(flow.getSteps(), ctx);
        return ctx;
    }

    private void runSteps(List<TaskStep> steps, Map<String, Object> ctx) {
        for (TaskStep step : steps) {
            // 1) Find pluginId
            PluginWrapper wrapper = pluginManager.whichPlugin(step.getClass());
            String pluginId = (wrapper != null ? wrapper.getPluginId() : "core");
            // 2) Load config if supported
            Map<String, Object> config = null;
            if (step instanceof ConfigurableTaskStep cfg) {
                config = cfg.getConfig();
            }
            String stepName = step.getClass().getSimpleName();
            System.out.printf("→ [%s][plugin=%s] initialize with config=%s%n",
                    stepName, pluginId, config);
            step.initialize(config);
            System.out.printf("→ [%s][plugin=%s] validate%n", stepName, pluginId);
            step.validate();
            System.out.printf("→ [%s][plugin=%s] entry with input=%s%n",
                    stepName, pluginId, ctx);
            step.entry(ctx);
            System.out.printf("→ [%s][plugin=%s] body start%n", stepName, pluginId);
            Object output = step.body(ctx);
            System.out.printf("→ [%s][plugin=%s] body returned=%s%n",
                    stepName, pluginId, output);
            // **MERGE THE STEP OUTPUT INTO ctx** **
            if (output instanceof Map<?, ?> mapOut) {
                ctx.putAll((Map<String, Object>) mapOut);
            } else if (output != null) {
                ctx.put(step.getId(), output);
            }

            System.out.printf("→ [%s][plugin=%s] exit%n", stepName, pluginId);
            step.exit(ctx);
        }

        System.out.println("=== Flow complete, result=" + ctx + " ===");
    }
}




