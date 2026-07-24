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
 * Auto-discovers all ModuleGenerator beans from Spring context.
 * No switch-case. No if-else. Just add a new class implementing ModuleGenerator.
 */
@Component
@Slf4j
public class ModuleGeneratorRegistry {

    private Map<String, ModuleGenerator> registry;

    @Autowired
    public ModuleGeneratorRegistry(List<ModuleGenerator> generators) {
        this.registry = generators.stream()
                .collect(Collectors.toMap(
                        g -> g.getModuleName().toUpperCase(),
                        Function.identity()
                ));
    }

    @PostConstruct
    public void logRegistered() {
        log.info("Registered module generators: {}", registry.keySet());
    }

    public ModuleGenerator getGenerator(String moduleName) {
        ModuleGenerator generator = registry.get(moduleName.toUpperCase());
        if (generator == null) {
            throw new IllegalArgumentException("No generator registered for module: " + moduleName
                    + ". Available: " + registry.keySet());
        }
        return generator;
    }

    public boolean isSupported(String moduleName) {
        return registry.containsKey(moduleName.toUpperCase());
    }
}
