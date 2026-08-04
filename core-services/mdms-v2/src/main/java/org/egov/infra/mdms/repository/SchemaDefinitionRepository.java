package org.egov.infra.mdms.repository;

import org.egov.infra.mdms.model.SchemaDefCriteria;
import org.egov.infra.mdms.model.SchemaDefinition;
import org.egov.infra.mdms.model.SchemaDefinitionRequest;
import org.egov.infra.mdms.model.SchemaDeleteRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemaDefinitionRepository {
    public void create(SchemaDefinitionRequest schemaDefinitionRequest);

    public void update(SchemaDefinitionRequest schemaDefinitionRequest);
    
    /**
     * Publishes schema delete request to Kafka.
     * Persister is responsible for audit logging and deletion.
     */
    void delete(SchemaDeleteRequest request);
        
    public List<SchemaDefinition> search(SchemaDefCriteria schemaDefCriteria);

    public Integer getTotalMastersCount(String tenantId);

    

}
