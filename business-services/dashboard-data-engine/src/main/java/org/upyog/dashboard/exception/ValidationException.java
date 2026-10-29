package org.upyog.dashboard.exception;

/**
 * Unchecked exception thrown when a validation rule fails anywhere in the
 * adapter-service pipeline.
 *
 * <p>This exception is raised by:
 * <ul>
 *   <li>{@link org.upyog.dashboard.validator.CommonValidator} — when mandatory
 *       cross-module fields (module, state, ULB, ward, region, metrics) are
 *       absent or empty in the transformed {@link org.upyog.dashboard.model.DashboardPayload}.</li>
 *   <li>{@link org.upyog.dashboard.validator.ModuleValidator} implementations
 *       (e.g. {@link org.upyog.dashboard.pt.validation.impl.PTValidator}) — when
 *       a required module-specific metric key is missing from the metrics dataMap.</li>
 * </ul>
 *
 * <p>Callers at the API layer should catch this exception and translate it to an
 * appropriate HTTP 400 / error response before returning to the client.
 *
 * <h3>Design note</h3>
 * Extending {@link RuntimeException} keeps validation errors unchecked, which
 * avoids propagating checked exceptions through every layer of the pipeline while
 * still allowing targeted catches where needed.
 *
 * @see org.upyog.dashboard.validator.CommonValidator
 * @see org.upyog.dashboard.validator.ModuleValidator
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code ValidationException} with the specified detail message.
     *
     * <p>The message should clearly identify which field failed validation and why,
     * for example: {@code "assessments is mandatory for PT module."} This message
     * is what callers will surface to end users or log for debugging.
     *
     * @param message a human-readable description of the validation failure;
     *                should identify the field and the rule that was violated;
     *                must not be {@code null}
     */
    public ValidationException(String message) {
        super(message);
    }
}
