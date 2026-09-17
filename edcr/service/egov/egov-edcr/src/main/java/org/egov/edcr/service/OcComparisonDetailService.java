package org.egov.edcr.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.egov.common.entity.dcr.helper.EdcrApplicationInfo;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Building;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.FloorDescription;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.PlanInformation;
import org.egov.edcr.entity.EdcrApplicationDetail;
import org.egov.edcr.entity.OcComparisonDetail;
import org.egov.edcr.repository.EdcrApplicationDetailRepository;
import org.egov.edcr.repository.OcComparisonDetailRepository;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Occupancy Certificate (OC) comparison details.
 * <p>
 * Handles persistence, flushing, and queries for {@link OcComparisonDetail} records
 * comparing original building permit scrutiny plans with occupancy certificate plans.
 * </p>
 *
 * @author eGovernments Foundation
 */
@Service
@Transactional(readOnly = true)
public class OcComparisonDetailService {

    public static final String FLOOR_DESC = "floorDesc";
    public static final String FLOOR_NO = "floorNo";

    @Autowired
    private OcComparisonDetailRepository ocComparisonDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    /**
     * Persists an {@link OcComparisonDetail} entity.
     *
     * @param ocComparisonDetail the entity to save
     */
    @Transactional
    public void save(OcComparisonDetail ocComparisonDetail) {
        ocComparisonDetailRepository.save(ocComparisonDetail);
    }
    
    /**
     * Persists an {@link OcComparisonDetail} entity and flushes changes instantly to the database.
     *
     * @param ocComparisonDetail the entity to save and flush
     */
    @Transactional
    public void saveAndFlush(OcComparisonDetail ocComparisonDetail) {
        ocComparisonDetailRepository.saveAndFlush(ocComparisonDetail);
    }

    /**
     * Persists a collection of {@link OcComparisonDetail} entities in batch.
     *
     * @param ocComparisonDetails the list of entities to save
     */
    @Transactional
    public void saveAll(List<OcComparisonDetail> ocComparisonDetails) {
        ocComparisonDetailRepository.saveAll(ocComparisonDetails);
    }

    /**
     * Finds an OC comparison detail by the permit DCR number.
     *
     * @param dcrNumber the permit scrutiny number
     * @return matching {@link OcComparisonDetail}, or {@code null}
     */
    public OcComparisonDetail findByDcrNumber(final String dcrNumber) {
        return ocComparisonDetailRepository.findByDcrNumber(dcrNumber);
    }

    /**
     * Finds an OC comparison detail by the occupancy certificate (OC) DCR number.
     *
     * @param ocdcrNumber the occupancy certificate scrutiny number
     * @return matching {@link OcComparisonDetail}, or {@code null}
     */
    public OcComparisonDetail findByOcDcrNumber(final String ocdcrNumber) {
        return ocComparisonDetailRepository.findByOcdcrNumber(ocdcrNumber);
    }

    /**
     * Finds an OC comparison detail by both OC DCR number and permit DCR number.
     *
     * @param ocdcrNumber the occupancy certificate scrutiny number
     * @param dcrNumber the permit scrutiny number
     * @return matching {@link OcComparisonDetail}, or {@code null}
     */
    public OcComparisonDetail findByOcDcrNoAndDcrNumber(final String ocdcrNumber, String dcrNumber) {
        return ocComparisonDetailRepository.findByOcdcrNumberAndDcrNumber(ocdcrNumber, dcrNumber);
    }

    /**
     * Finds an OC comparison detail by OC DCR number, permit DCR number, and tenant ID.
     *
     * @param ocdcrNumber the occupancy certificate scrutiny number
     * @param dcrNumber the permit scrutiny number
     * @param tenantId the tenant/ULB identifier
     * @return matching {@link OcComparisonDetail}, or {@code null}
     */
    public OcComparisonDetail findByOcDcrNoAndDcrNumberAndTenant(final String ocdcrNumber, String dcrNumber, String tenantId) {
        return ocComparisonDetailRepository.findByOcdcrNumberAndDcrNumberAndTenantId(ocdcrNumber, dcrNumber, tenantId);
    }

}
