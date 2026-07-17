package org.upyog.adapter.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.transformer.ModuleTransformer;

/**
 * Spring-managed registry that maps each {@link Module} to its concrete
 * {@link ModuleTransformer} implementation.
 *
 * <p>The registry is populated once at application startup via constructor
 * injection: Spring collects all beans that implement {@link ModuleTransformer},
 * and the constructor iterates over them to build the internal
 * {@link #registry} map.  This means adding a new module transformer is as
 * simple as annotating the new class with {@code @Component} — no manual
 * registration or factory changes are required.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * ModuleTransformer<Object> transformer = transformerRegistry.get(Module.PT);
 * DashboardPayload payload = transformer.transform(rawData);
 * }</pre>
 *
 * <h3>Thread safety</h3>
 * The {@link #registry} map is populated in the constructor and never mutated
 * afterwards, making reads from {@link #get(Module)} inherently thread-safe
 * without synchronization.
 *
 * @see ModuleTransformer
 * @see Module
 * @see org.upyog.adapter.validator.ValidatorRegistry
 */
/**
 * Class representing the TransformerRegistry class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Component
public class TransformerRegistry {

    /**
     * Internal map from {@link Module} to its registered {@link ModuleTransformer}.
     *
     * <p>Populated once during construction; never mutated after startup.
     */
    private final Map<Module, ModuleTransformer<?>> registry = new HashMap<>();

    /**
     * Constructs the registry by iterating over all {@link ModuleTransformer}
     * beans discovered by Spring and mapping each to its declared module.
     *
     * <p>If two transformers declare the same {@link Module}, the last one
     * encountered overwrites the earlier one.  To avoid silent overwrites,
     * ensure each module has exactly one transformer bean.
     *
     * @param transformers the list of all {@link ModuleTransformer} beans in the
     *                     Spring application context; injected automatically
     *                     by Spring via constructor injection
     */
    public TransformerRegistry(List<ModuleTransformer<?>> transformers) {
        transformers.forEach(t -> registry.put(t.getModule(), t));
    }

    /**
     * Looks up and returns the {@link ModuleTransformer} registered for the
     * given {@code module}.
     *
     * <p>The return type is cast to {@code ModuleTransformer<T>} using an
     * unchecked cast.  Type safety is maintained by convention: every
     * {@link Module} value has exactly one transformer registered, and callers
     * must supply {@code rawData} of the type expected by that transformer.
     *
     * @param <T>    the raw-data type that the returned transformer accepts
     * @param module the module whose transformer to look up; must not be
     *               {@code null}
     * @return the {@link ModuleTransformer} registered for {@code module};
     *         never {@code null}
     * @throws IllegalArgumentException if no transformer is registered for the
     *                                  given {@code module}
     */
    @SuppressWarnings("unchecked")
    public <T> ModuleTransformer<T> get(Module module) {

        ModuleTransformer<?> transformer = registry.get(module);

        if (transformer == null) {
            throw new IllegalArgumentException(
                    "No transformer found for module : " + module);
        }

        return (ModuleTransformer<T>) transformer;
    }
}
