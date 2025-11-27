package org.egov.asset.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.kafka.Producer;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class AssetInventoryRepository {

    @Autowired
    private AssetConfiguration config;

    @Autowired
    private Producer producer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Pushes the request on save inventory topic through kafka
     *
     * @param assetRequest The asset inventory create request
     */
    public void saveInventory(AssetRequest assetRequest) {
        producer.push("save-inventory", assetRequest);
    }

    /**
     * Pushes the request on update inventory topic through kafka
     *
     * @param assetRequest The asset inventory update request
     */
    public void updateInventory(AssetRequest assetRequest) {
        producer.push("update-inventory", assetRequest);
    }

    /**
     * Searches for asset inventory records
     *
     * @param assetRequest The asset inventory search request
     * @return List of assets with inventory data
     */
    public List<Asset> searchInventory(AssetRequest assetRequest) {
        // For now, return empty list - implement actual search logic as needed
        return new ArrayList<>();
    }

    /**
     * Get JdbcTemplate for direct database queries
     *
     * @return JdbcTemplate instance
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}