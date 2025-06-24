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
public interface DepartmentRepository extends JpaRepository<Department, Long> , JpaSpecificationExecutor<Department>{
   

}