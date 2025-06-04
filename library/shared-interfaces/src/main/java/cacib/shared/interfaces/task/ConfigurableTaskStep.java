package cacib.shared.interfaces.task;


import java.util.Map;

/**
 * A TaskStep that receives a config map from YAML→Java.
 */
public interface ConfigurableTaskStep extends TaskStep {
    void setConfig(Map<String,Object> config);
    Map<String,Object> getConfig();
}
