package org.upyog.reconciliation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upyog.reconciliation.model.ReconExtractionDetail;

@Repository
public interface ReconExtractionDetailRepository extends JpaRepository<ReconExtractionDetail, Long> {
}
