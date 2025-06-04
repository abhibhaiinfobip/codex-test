package cacib.task.manager;

import cacib.shared.interfaces.task.TaskFlowDefinition;
import cacib.shared.interfaces.task.TaskStep;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pf4j.PluginManager;
import org.pf4j.spring.SpringPluginManager;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.swing.*;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Loads flow definitions from YAML and creates TaskFlowDefinitionImpl instances,
 * resolving each step to a plugin‐provided TaskStep via PF4J.
 */
public class TaskManagerFlowLoader {

    private final CustomPluginManager pluginManager;

    public TaskManagerFlowLoader(CustomPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public List<TaskFlowDefinition> loadFlows() {
        // --- YAML setup (unchanged) ---
        LoaderOptions options = new LoaderOptions();
        Constructor constructor = new Constructor(TaskFlowYamlWrapper.class, options);

        TypeDescription wrapperDesc = new TypeDescription(TaskFlowYamlWrapper.class);
        wrapperDesc.addPropertyParameters("flows", FlowYaml.class);
        constructor.addTypeDescription(wrapperDesc);

        TypeDescription flowDesc = new TypeDescription(FlowYaml.class);
        flowDesc.addPropertyParameters("steps", StepYaml.class);
        constructor.addTypeDescription(flowDesc);

        Yaml yaml = new Yaml(constructor);
        InputStream input = getClass().getClassLoader().getResourceAsStream("flow.yaml");
        if (input == null) {
            throw new RuntimeException("flow.yaml not found");
        }
        TaskFlowYamlWrapper wrapper = yaml.load(input);

        // map each FlowYaml → TaskFlowDefinitionImpl
        return wrapper.getFlows().stream()
                .map(this::toDefinition)
                .collect(Collectors.toList());
    }

    private TaskFlowDefinitionImpl toDefinition(FlowYaml flowYaml) {
        TaskFlowDefinitionImpl def = new TaskFlowDefinitionImpl();
        def.setFlowId(flowYaml.getFlowId());

        List<TaskStep> steps = flowYaml.getSteps().stream()
                .map(this::resolveAndInitStep)
                .collect(Collectors.toList());

        def.setSteps(steps);
        return def;
    }

    private TaskStep resolveAndInitStep(StepYaml sy) {
        String wanted = sy.getStepId().trim();
        List<TaskStep> all = pluginManager.getExtensions(TaskStep.class);

        if (all.isEmpty()) {
            throw new IllegalStateException(
                    "No TaskStep extensions found — did you point PF4J at the plugin folder?"
            );
        }

        Optional<TaskStep> found = all.stream()
                .filter(ext ->
                        // allow either class‐name or your getId()
                        wanted.equalsIgnoreCase(ext.getClass().getSimpleName()) ||
                                wanted.equalsIgnoreCase(ext.getId())
                )
                .findFirst();

        if (found.isEmpty()) {
            String available = all.stream()
                    .map(ext -> ext.getClass().getSimpleName() + " (id=" + ext.getId() + ")")
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException(
                    "No TaskStep for ID '" + wanted + "' found. Available: " + available
            );
        }

        TaskStep step = found.get();
        step.initialize(sy.getConfig());
        return step;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskFlowYamlWrapper {
        private List<FlowYaml> flows;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class FlowYaml {
        private String flowId;
        private List<StepYaml> steps;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class StepYaml {
        private String stepId;
        private Map<String, Object> config;
    }

}
