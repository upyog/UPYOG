package org.egov.loadgenerator.generator;

import java.util.Map;

/**
 * Every module must implement this interface.
 * No switch-case or if-else needed anywhere.
 * Just implement this and Spring auto-registers it.
 */
public interface ModuleGenerator {

    default String getUpdateApiUrl() {
        throw new UnsupportedOperationException("Update API is not implemented");
    }

    default String getSearchApiUrl() {
    throw new UnsupportedOperationException("Search API is not implemented");
}

    String getModuleName();

    String getCreateApiUrl();
    

    Object buildPayload(String tenantId, int index);
}
