package org.egov.gis.service;

import org.egov.gis.interfaces.MunicipalServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service Registry for managing different municipal service adapters
 * Implements Registry pattern for dynamic service discovery
 */
@Service
public class ServiceRegistry {

    @Autowired
    private List<MunicipalServiceAdapter> adapters;

    private Map<String, MunicipalServiceAdapter> adapterMap = new HashMap<>();

    @PostConstruct
    public void initializeAdapters() {
        for (MunicipalServiceAdapter adapter : adapters) {
            adapterMap.put(adapter.getBusinessService(), adapter);
        }
    }

    /**
     * Get adapter for a specific business service
     */
    public MunicipalServiceAdapter getAdapter(String businessService) {
        MunicipalServiceAdapter adapter = adapterMap.get(businessService);
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter found for business service: " + businessService);
        }
        return adapter;
    }

    /**
     * Get all available business services
     */
    public List<String> getAvailableBusinessServices() {
        return new ArrayList<>(adapterMap.keySet());
    }

    /**
     * Check if adapter exists for business service
     */
    public boolean hasAdapter(String businessService) {
        return adapterMap.containsKey(businessService);
    }
}
