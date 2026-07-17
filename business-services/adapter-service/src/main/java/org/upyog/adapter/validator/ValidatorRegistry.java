package org.upyog.adapter.validator;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.upyog.adapter.common.constants.Module;

/**
 * Spring-managed registry that maps each {@link Module} to its concrete
 * {@link ModuleValidator} implementation.
 *
 * <p>The registry is populated once at application startup via constructor
 * injection: Spring collects all beans that implement {@link ModuleValidator},
 * and the constructor iterates over them to build the internal
 * {@link #validators} map.  Adding a new module validator is therefore as simple
 * as annotating the new class with {@code @Component} — no manual registration
 * is required.
 *
 * <h3>Backing map</h3>
 * An {@link EnumMap} is used instead of a general {@link java.util.HashMap}
 * because all keys are {@link Module} enum constants.  {@code EnumMap} provides
 * O(1) lookup backed by an array, making it both faster and more memory-efficient
 * than a hash-based map for a small, fixed set of enum keys.
 *
 * <h3>Thread safety</h3>
 * The {@link #validators} map is populated in the constructor and never mutated
 * afterwards, making reads from {@link #get(Module)} inherently thread-safe.
 *
 * <h3>Usage in the pipeline</h3>
 * Currently commented out in {@link org.upyog.adapter.api.AdapterClientImpl}
 * pending completion of all active module validators.  Once all modules have
 * validators the following call should be re-enabled:
 * <pre>{@code
 * validatorRegistry.get(request.getModule())
 *                  .validate(payload.getData().get(0).getMetrics());
 * }</pre>
 *
 * @see ModuleValidator
 * @see org.upyog.adapter.registry.TransformerRegistry
 */
@Component
public class ValidatorRegistry {

    /**
     * Internal map from {@link Module} to its registered {@link ModuleValidator}.
     *
     * <p>Uses {@link EnumMap} for efficient constant-time lookup.
     * Populated once in the constructor; immutable after startup.
     */
    private final Map<Module, ModuleValidator> validators = new EnumMap<>(Module.class);

    /**
     * Constructs the registry by iterating over all {@link ModuleValidator} beans
     * discovered by Spring and mapping each to its declared module.
     *
     * <p>If two validators declare the same {@link Module}, the last one
     * encountered overwrites the earlier one silently.  Ensure each module has
     * exactly one validator bean to avoid this.
     *
     * @param validatorList the list of all {@link ModuleValidator} beans in the
     *                      Spring application context; injected automatically
     *                      by Spring via constructor injection
     */
    public ValidatorRegistry(List<ModuleValidator> validatorList) {
        validatorList.forEach(v -> validators.put(v.getModule(), v));
    }

    /**
     * Looks up and returns the {@link ModuleValidator} registered for the
     * given {@code module}.
     *
     * @param module the module whose validator to retrieve; must not be
     *               {@code null}
     * @return the {@link ModuleValidator} registered for {@code module};
     *         never {@code null}
     * @throws IllegalArgumentException if no validator is registered for the
     *                                  given {@code module}
     */
    public ModuleValidator get(Module module) {

        ModuleValidator validator = validators.get(module);

        if (validator == null) {
            throw new IllegalArgumentException(
                    "Validator not found for module : " + module);
        }

        return validator;
    }
}
