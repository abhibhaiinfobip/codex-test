package cacib.shared.interfaces.task;

import org.pf4j.ExtensionPoint;

import java.util.Map;

public interface FlowEntryPoint extends ExtensionPoint {
    /**
     * Entry points must implement this method.
     * It handles pre-processing, validation, and invokes TaskManager.
     *
     * @param input Raw input from the entry point (e.g., HTTP request payload)
     * @return Result after workflow execution.
     */
    Map<String, Object> handleEntry(Map<String, Object> input);
}
