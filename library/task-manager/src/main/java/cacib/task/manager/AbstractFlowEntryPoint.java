package cacib.task.manager;

import cacib.shared.interfaces.task.FlowEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public abstract class AbstractFlowEntryPoint implements FlowEntryPoint  {

    @Autowired
    protected TaskManager taskManager;

    /**
     * The flowId to execute, configured in concrete subclasses.
     */
    protected abstract String getFlowId();

    /**
     * Common handling logic across all entry points.
     */
    @Override
    public Map<String, Object> handleEntry(Map<String, Object> input) {
        preProcess(input);
        return taskManager.executeFlow(getFlowId(), input);
    }

    /**
     * Pre-processing steps common to all entry points.
     */
    protected void preProcess(Map<String, Object> input) {
        // e.g., logging, validation, enrichment
    }
}
