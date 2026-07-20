package org.egov.infra.mdms.repository;

import org.egov.infra.mdms.model.SchemaDefCriteria;
import org.egov.infra.mdms.model.SchemaDefinition;
import org.egov.infra.mdms.model.SchemaDefinitionRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemaDefinitionRepository {
    public void create(SchemaDefinitionRequest schemaDefinitionRequest);

    public void update(SchemaDefinitionRequest schemaDefinitionRequest);

    void delete(String tenantId, String code);

    public List<SchemaDefinition> search(SchemaDefCriteria schemaDefCriteria);

    public Integer getTotalMastersCount(String tenantId);

    

}
