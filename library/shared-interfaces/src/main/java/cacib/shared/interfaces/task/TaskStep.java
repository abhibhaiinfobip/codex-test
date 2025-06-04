package cacib.shared.interfaces.task;

import org.pf4j.ExtensionPoint;

import java.util.Map;

/**
 * A single unit of work that can be initialized, validated, run (body), and exited.
 */
public interface TaskStep extends ExtensionPoint {

    /** Uniquely identifies this step. Used as a key if body() returns a non‐map. */
    String getId();

    /**
     * Called before validate(body/exit). Supply step‐specific config.
     * If you don’t implement ConfigurableTaskStep, you can ignore config.
     */
    void initialize(Map<String, Object> config);

    /**
     * Called after initialize, before entry. Throw if required config is missing.
     */
    void validate();

    /**
     * Called just before body(); input is the shared context.
     */
    void entry(Map<String, Object> ctx);

    /**
     * The main logic of your step. Can access & mutate ctx, or return a Map whose keys get merged.
     */
    Object body(Map<String, Object> ctx);

    /** Called immediately after body(), with the same context. */
    void exit(Map<String, Object> ctx);
}
