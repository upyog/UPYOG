package org.egov.inbox.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * ModuleHandlerRegistry is responsible for
 * managing and resolving module handlers.
 */
@Slf4j
@Component
public class ModuleHandlerRegistry {

    private final List<ModuleInboxHandler> handlers;

    /**
     * Initializes module handler registry.
     *
     * @param handlers registered module handlers
     */
    @Autowired
    public ModuleHandlerRegistry(List<ModuleInboxHandler> handlers) {
        this.handlers = handlers;

        log.info("ModuleHandlerRegistry initialized with {} handlers: {}",
                handlers.size(),
                handlers.stream()
                        .map(h -> h.getClass().getSimpleName())
                        .toList());
    }

    /**
     * Returns module handler for the given module.
     *
     * @param moduleName module name
     * @return matching module handler
     */
    public Optional<ModuleInboxHandler> getHandler(String moduleName) {

        if (moduleName == null)
            return Optional.empty();
        return handlers.stream().filter(h -> h.supports(moduleName)).findFirst();
    }

    /**
     * Checks whether handler exists for the module.
     *
     * @param moduleName module name
     * @return true if handler exists
     */
    public boolean hasHandler(String moduleName) {
        return getHandler(moduleName).isPresent();
    }

    /**
     * Returns internal workflow module name
     * for the given module.
     *
     * @param moduleName module name
     * @return internal module name
     */
    public String getModuleName(String moduleName) {

        if (moduleName == null)
            return null;

        return handlers.stream()
                .filter(h -> h.supports(moduleName))
                .findFirst()
                .map(h -> h.getInternalModuleName(moduleName))
                .orElse(moduleName);
    }

    /**
     * Checks whether workflow total count
     * is required for the module.
     *
     * @param moduleName module name
     * @return workflow total count required flag
     */
    public boolean isWorkflowTotalCountRequired(String moduleName) {

        return getHandler(moduleName)
                .map(ModuleInboxHandler::isWorkflowTotalCountRequired)
                .orElse(true);
    }

    /**
     * Checks whether workflow nearing SLA count
     * is required for the module.
     *
     * @param moduleName module name
     * @return workflow nearing SLA count required flag
     */
    public boolean isWorkflowNearingSlaCountRequired(String moduleName) {

        return getHandler(moduleName)
                .map(ModuleInboxHandler::isWorkflowNearingSlaCountRequired)
                .orElse(true);
    }
}