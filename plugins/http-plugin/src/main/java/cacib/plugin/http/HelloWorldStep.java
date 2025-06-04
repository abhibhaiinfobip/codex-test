package cacib.plugin.http;

import cacib.shared.interfaces.task.TaskStep;
import org.pf4j.Extension;

import java.util.Map;

@Extension
public class HelloWorldStep implements TaskStep {
    @Override
    public String getId() {
        return "helloWorldFlow";
    }

    @Override
    public void initialize(Map<String, Object> config) { /*…*/ }

    @Override
    public void validate() { /*…*/ }

    @Override
    public void entry(Map<String, Object> context) { /*…*/ }

    @Override
    public Map<String, Object> body(Map<String, Object> context) {
        return Map.of("message", "Hello");
    }

    @Override
    public void exit(Map<String, Object> context) { /*…*/ }
}
