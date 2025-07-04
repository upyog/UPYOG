/**
 * Created on Jun 19, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.repository;

import org.egov.finance.inbox.entity.AppConfigValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigValuesRepository extends JpaRepository<AppConfigValues, Long>, JpaSpecificationExecutor<AppConfigValues> {

}

