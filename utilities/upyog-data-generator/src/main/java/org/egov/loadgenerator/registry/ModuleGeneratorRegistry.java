package org.egov.loadgenerator.registry;

import lombok.extern.slf4j.Slf4j;
import org.egov.loadgenerator.generator.ModuleGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Maintains a registry of all {@link ModuleGenerator} implementations
 * available in the Spring application context.
 *
 * <p>During application startup, all module generators are automatically
 * discovered and mapped using their module names. This eliminates the
 * need for manual registration or conditional logic when resolving
 * module-specific payload generators.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Automatically register all ModuleGenerator implementations.</li>
 *   <li>Provide lookup of generators by module name.</li>
 *   <li>Validate whether a module is supported.</li>
 *   <li>Log registered modules during application startup.</li>
 * </ul>
 *
 * <h3>Thread Safety</h3>
 * <p>The registry is initialized once during application startup and
 * remains effectively immutable for the lifetime of the application.
 *
 * @see ModuleGenerator
 */
@Component
@Slf4j
public class ModuleGeneratorRegistry {

    private Map<String, ModuleGenerator> registry;

    /**
     * Creates the module generator registry by discovering all
     * {@link ModuleGenerator} implementations managed by Spring.
     *
     * @param generators the list of available module generators
     */
    @Autowired
    public ModuleGeneratorRegistry(List<ModuleGenerator> generators) {
        this.registry = generators.stream()
                .collect(Collectors.toMap(
                        g -> g.getModuleName().toUpperCase(),
                        Function.identity()
                ));
    }

    /**
     * Logs all registered module generators after the registry
     * has been initialized.
     */
    @PostConstruct
    public void logRegistered() {
        log.info("Registered module generators: {}", registry.keySet());
    }

    /**
     * Returns the module generator associated with the specified module.
     *
     * <p>If no generator is registered for the supplied module name,
     * an {@link IllegalArgumentException} is thrown.
     *
     * @param moduleName the module identifier
     * @return the corresponding module generator
     * @throws IllegalArgumentException if no generator is registered
     *         for the specified module
     */
    public ModuleGenerator getGenerator(String moduleName) {
        ModuleGenerator generator = registry.get(moduleName.toUpperCase());
        if (generator == null) {
            throw new IllegalArgumentException(
                    "No generator registered for module: "
                            + moduleName
                            + ". Available: "
                            + registry.keySet());
        }
        return generator;
    }

    /**
     * Determines whether the specified module is supported.
     *
     * @param moduleName the module identifier
     * @return {@code true} if a generator is registered for the module;
     *         {@code false} otherwise
     */
    public boolean isSupported(String moduleName) {
        return registry.containsKey(moduleName.toUpperCase());
    }
}
