package org.upyog.reconciliation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upyog.reconciliation.model.ReconConfiguration;

import java.util.List;

@Repository
public interface ReconConfigurationRepository extends JpaRepository<ReconConfiguration, Long> {
    List<ReconConfiguration> findByIsActiveTrue();
}
