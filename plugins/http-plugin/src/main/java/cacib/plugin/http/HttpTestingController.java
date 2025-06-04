package cacib.plugin.http;

import cacib.task.manager.TaskManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class HttpTestingController {

    private final ObjectProvider<TaskManager> taskManagerProvider;

    public HttpTestingController(ObjectProvider<TaskManager> taskManagerProvider) {
        this.taskManagerProvider = taskManagerProvider;
    }

    /**
     * Example endpoint:
     *   POST /test/run
     *   Content-Type: application/json
     *   Body: { "foo": "bar", "count": 123, ... }
     *
     * It will look up TaskManager at runtime, and if available, call
     * tm.executeFlow("helloWorldFlow", inputMap) and return its result.
     */
    @PostMapping("/run")
    public ResponseEntity<?> runHelloWorldFlow(@RequestBody(required = false) Map<String, Object> input) {
        TaskManager tm = taskManagerProvider.getIfAvailable();
        if (tm == null) {
            // TaskManager isn’t ready yet; return 503 with an error message
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "TaskManager not available"));
        }

        // If no JSON body was sent, use an empty map
        if (input == null) {
            input = Map.of();
        }

        // Execute the flow, passing in the input map directly
        Map<String, Object> result;
        try {
            result = tm.executeFlow("helloWorldFlow", input);
        } catch (Exception e) {
            // If your flow can throw a ValidationException or other errors,
            // you might want to catch and return a 4xx or 5xx here.
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Failed to execute flow",
                            "message", e.getMessage()
                    ));
        }

        return ResponseEntity.ok("HelloWorkldFlow is executing");
    }
}
