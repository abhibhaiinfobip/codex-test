package cacib.task.manager;

import cacib.shared.interfaces.task.TaskFlowDefinition;
import cacib.shared.interfaces.task.TaskStep;

import java.util.List;

/**
 * A simple in‐memory implementation of TaskFlowDefinition.
 */
public class TaskFlowDefinitionImpl implements TaskFlowDefinition {

    private String flowId;
    private List<TaskStep> steps;

    @Override
    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    @Override
    public List<TaskStep> getSteps() {
        return steps;
    }

    public void setSteps(List<TaskStep> steps) {
        this.steps = steps;
    }
}
