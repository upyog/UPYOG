/**
 * Created on May 30, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.repository;

import org.egov.finance.inbox.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundRepository extends JpaRepository<Fund, Long> , JpaSpecificationExecutor<Fund>{
    Fund findByName(String name);

    Fund findByCode(String code);

    List<Fund> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrIsactive(String name, String code,
            Boolean isactive);

    List<Fund> findByCodeContainingIgnoreCase(String code);

    List<Fund> findByIsactive(Boolean isactive);

    List<Fund> findByNameContainingIgnoreCase(String name);

    List<Fund> findByIsnotleaf(Boolean isnotleaf);

    List<Fund> findByIsactiveAndIsnotleaf(final Boolean active, final Boolean isNotLeaf);

}