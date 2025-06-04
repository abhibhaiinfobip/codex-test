package cacib.shared.interfaces.task;

import java.util.List;

/**
 * A simple flow definition:
 *   • getFlowId()
 *   • getSteps()
 */
public interface TaskFlowDefinition {
    String getFlowId();
    List<TaskStep> getSteps();
}
