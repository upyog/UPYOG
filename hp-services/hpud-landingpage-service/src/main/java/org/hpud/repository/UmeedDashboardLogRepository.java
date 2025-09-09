package org.hpud.repository;

import org.hpud.entity.UmeedDashboardLogger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UmeedDashboardLogRepository extends JpaRepository<UmeedDashboardLogger, Long> {
	
}
