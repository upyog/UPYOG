package org.egov.edcr.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.entity.edcr.LayerName;
import org.egov.commons.service.LayerNameService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PreDestroy;

/**
 * In-memory cache of EDCR layer name key-to-display-value mappings.
 * <p>
 * Mappings are loaded lazily from the database on the first {@link #getLayerName(String)}
 * call rather than at application startup. This avoids JTA datasource errors on WildFly
 * ({@code IJ000460}) when connections are requested before a transaction is available.
 * </p>
 */
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Service
public class LayerNames {

    private final Map<String, String> layerNamesMap = new HashMap<>();
    private final LayerNameService layerNameService;
    private final TransactionTemplate transactionTemplate;
    private volatile boolean loaded;

    /**
     * @param layerNameService service used to fetch layer name records from the database
     * @param transactionTemplate used to run the initial load inside a JTA transaction
     */
    public LayerNames(LayerNameService layerNameService, TransactionTemplate transactionTemplate) {
        this.layerNameService = layerNameService;
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * Returns the display name for the given layer name key.
     * <p>
     * Triggers a one-time load of all layer name mappings from the database if the
     * cache has not yet been populated.
     * </p>
     *
     * @param layerNameKey the layer name key to look up
     * @return the mapped display value, or {@code null} if no mapping exists for the key
     */
    public String getLayerName(String layerNameKey) {
        ensureLoaded();
        return layerNamesMap.get(layerNameKey);
    }

    /**
     * Populates {@link #layerNamesMap} from the database if not already loaded.
     * <p>
     * Uses double-checked locking and runs the query inside an explicit JTA transaction
     * so WildFly's {@code java:/READWRITE_DS} datasource can enlist the connection.
     * </p>
     */
    private void ensureLoaded() {
        if (loaded) {
            return;
        }
        synchronized (this) {
            if (loaded) {
                return;
            }
            transactionTemplate.execute(status -> {
                List<LayerName> layerNames = layerNameService.findAll();
                for (LayerName layerName : layerNames) {
                    layerNamesMap.put(layerName.getKey(), layerName.getValue());
                }
                return null;
            });
            loaded = true;
        }
    }

    /**
     * Clears the in-memory cache when the Spring context is shut down.
     */
    @PreDestroy
    public void destroy() {
        layerNamesMap.clear();
        loaded = false;
    }

}