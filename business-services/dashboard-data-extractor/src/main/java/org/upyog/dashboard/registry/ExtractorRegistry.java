package org.upyog.dashboard.registry;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.extractor.ModuleExtractor;

/**
 * Registry mapping each business {@link Module} to its corresponding {@link ModuleExtractor}.
 * 
 * <p>Uses Spring constructor dataList injection to auto-discover all available {@link ModuleExtractor}
 * beans at application startup. Adding a new module extractor requires zero manual registration.
 */
@Component
public class ExtractorRegistry {

    private final Map<Module, ModuleExtractor<?>> extractors = new EnumMap<>(Module.class);

    /**
     * Constructs the ExtractorRegistry by discovering all {@link ModuleExtractor} Spring components.
     * 
     * @param extractorList dataList of all ModuleExtractor components discovered by Spring
     */

    public ExtractorRegistry(List<ModuleExtractor<?>> extractorList) {
        for (ModuleExtractor<?> extractor : extractorList) {
            extractors.put(extractor.getModule(), extractor);
        }
    }

    /**
     * Looks up the registered {@link ModuleExtractor} for the specified module.
     * 
     * @param module the module to look up
     * @return the module extractor instance, or {@code null} if not registered
     */
    public ModuleExtractor<?> get(Module module) {
        return extractors.get(module);
    }
}
