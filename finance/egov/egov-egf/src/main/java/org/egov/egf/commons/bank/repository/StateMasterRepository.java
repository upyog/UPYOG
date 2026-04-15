package org.egov.egf.commons.bank.repository;

import org.egov.commons.Bank;
import org.egov.commons.StateMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author shahrukk
 *
 */

@Repository
public interface StateMasterRepository extends JpaRepository<StateMaster, Integer>{

}
