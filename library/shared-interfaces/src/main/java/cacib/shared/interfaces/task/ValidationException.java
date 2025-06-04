package cacib.shared.interfaces.task;

/**
 * Exception to indicate validation failure in a plugin step.
 * <p>
 * This exception should be thrown when a validation check fails within a task step implementation.
 * It can be used to signal invalid input, configuration errors, or any other validation-related issue
 * that prevents the step from proceeding.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 *     if (!isValid(input)) {
 *         throw new ValidationException("Input is invalid");
 *     }
 * </pre>
 * </p>
 */
public class ValidationException extends Exception {
    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
