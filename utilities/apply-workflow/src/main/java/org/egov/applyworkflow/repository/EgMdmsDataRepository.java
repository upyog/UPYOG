package org.egov.applyworkflow.repository;

import org.egov.applyworkflow.web.model.EgMdmsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EgMdmsDataRepository extends JpaRepository<EgMdmsData, String> {

    @Query("SELECT e.data FROM EgMdmsData e WHERE e.tenantId = :tenantId AND e.uniqueIdentifier = :uniqueIdentifier")
    String findDataByUniqueIdentifier(@Param("tenantId") String tenantId,
                                      @Param("uniqueIdentifier") String uniqueIdentifier);
}