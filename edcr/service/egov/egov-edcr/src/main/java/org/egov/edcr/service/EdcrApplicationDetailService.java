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
import org.egov.edcr.repository.EdcrApplicationDetailRepository;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing {@link EdcrApplicationDetail} lifecycle and queries.
 * <p>
 * Provides database operations for saving application details, querying by DCR numbers,
 * transaction numbers, and third-party user identifiers, and constructing structural
 * building models for approved plans.
 * </p>
 *
 * @author eGovernments Foundation
 */
@Service
@Transactional(readOnly = true)
public class EdcrApplicationDetailService {

    public static final String FLOOR_DESC = "floorDesc";
    public static final String FLOOR_NO = "floorNo";

    @Autowired
    private EdcrApplicationDetailRepository edcrApplicationDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    /**
     * Persists an individual {@link EdcrApplicationDetail} entity to the database.
     *
     * @param edcrApplicationDetail the application detail entity to persist
     */
    @Transactional
    public void save(EdcrApplicationDetail edcrApplicationDetail) {
        edcrApplicationDetailRepository.save(edcrApplicationDetail);
    }

    /**
     * Persists a collection of {@link EdcrApplicationDetail} entities to the database in batch.
     *
     * @param edcrApplicationDetails the list of application detail entities to save
     */
    @Transactional
    public void saveAll(List<EdcrApplicationDetail> edcrApplicationDetails) {
        edcrApplicationDetailRepository.saveAll(edcrApplicationDetails);
    }

    /**
     * Retrieves all application details associated with a given parent application ID.
     *
     * @param dcrApplicationId the unique identifier of the parent EDCR application
     * @return a list of associated {@link EdcrApplicationDetail} entities
     */
    public List<EdcrApplicationDetail> fingByDcrApplicationId(Long dcrApplicationId) {
        return edcrApplicationDetailRepository.findByApplicationId(dcrApplicationId);
    }

    /**
     * Retrieves an application detail record by its unique building plan scrutiny (DCR) number.
     *
     * @param dcrNumber the unique building plan scrutiny number
     * @return the matching {@link EdcrApplicationDetail}, or {@code null} if not found
     */
    public EdcrApplicationDetail findByDcrNumber(final String dcrNumber) {
        return edcrApplicationDetailRepository.findByDcrNumber(dcrNumber);
    }

    /**
     * Retrieves an application detail by DCR number and third-party user code.
     *
     * @param dcrNumber the unique building plan scrutiny number
     * @param thirdPartyUserCode the user code or UUID of the third-party portal user
     * @return the matching {@link EdcrApplicationDetail}, or {@code null} if not found
     */
    public EdcrApplicationDetail findByDcrNumberAndTPUserCode(final String dcrNumber, final String thirdPartyUserCode) {
        return edcrApplicationDetailRepository.findByDcrNumberAndApplication_ThirdPartyUserCode(dcrNumber, thirdPartyUserCode);
    }

    /**
     * Retrieves an application detail by DCR number and transaction number.
     *
     * @param dcrNumber the unique building plan scrutiny number
     * @param transactionNumber the client-side transaction identifier
     * @return the matching {@link EdcrApplicationDetail}, or {@code null} if not found
     */
    public EdcrApplicationDetail findByDcrAndTransactionNumber(final String dcrNumber, final String transactionNumber) {
        return edcrApplicationDetailRepository.findByDcrNumberAndApplication_TransactionNumber(dcrNumber, transactionNumber);
    }
    
    /**
     * Retrieves an application detail by DCR number, transaction number, and third-party user code.
     *
     * @param dcrNumber the unique building plan scrutiny number
     * @param transactionNumber the client-side transaction identifier
     * @param thirdPartyUserCode the user code or UUID of the third-party portal user
     * @return the matching {@link EdcrApplicationDetail}, or {@code null} if not found
     */
    public EdcrApplicationDetail findByDcrAndTransactionNumberAndTPUserCode(final String dcrNumber, final String transactionNumber, final String thirdPartyUserCode) {
        return edcrApplicationDetailRepository.findByDcrNumberAndApplication_TransactionNumberAndApplication_ThirdPartyUserCode(dcrNumber, transactionNumber, thirdPartyUserCode);
    }
    
    /**
     * Retrieves an application detail by DCR number and third-party user tenant.
     *
     * @param dcrNumber the unique building plan scrutiny number
     * @param thirdPartyUserTenant the tenant ID of the third-party user
     * @return the matching {@link EdcrApplicationDetail}, or {@code null} if not found
     */
    public EdcrApplicationDetail findByDcrNumberAndTPUserTenant(final String dcrNumber, final String thirdPartyUserTenant) {
        return edcrApplicationDetailRepository.findByDcrNumberAndApplication_ThirdPartyUserTenant(dcrNumber, thirdPartyUserTenant);
    }

    /**
     * Constructs and populates building, floor, occupancy, and plan entities for approved plans
     * and maps them into the provided {@link EdcrApplicationInfo}.
     *
     * @param edcrApplicationDetail the persistent application detail record
     * @param applicationInfo the target application info DTO to populate
     */
    public void buildBuildingDetailForApprovedPlans(EdcrApplicationDetail edcrApplicationDetail,
            EdcrApplicationInfo applicationInfo) {
        final Map<String, Long> params = new HashMap<>();
        final StringBuilder queryApplnBldgDtls = new StringBuilder(1000);
        queryApplnBldgDtls
                .append("select appln.applicationnumber, appln.occupancy, appln.servicetype, appln.applicantname, appln.architectinformation, appdtl.dcrnumber, "
                        + "appdtl.dxffileid, appdtl.reportoutputid, building.id as bldgId, building.buildingheight, building.totalfloors, pi.plotArea, building.floorsaboveground, plan.id as planId from edcr_application_detail appdtl "
                        + " left outer join edcr_application appln on appdtl.application = appln.id"
                        + " left outer join edcr_plan_detail plan on appdtl.plandetail = plan.id"
                        + " left outer join edcr_planinfo pi on plan.planinformation = pi.id"
                        + " left outer join edcr_building building on plan.building = building.id"
                        + " where appdtl.id=:applicationDetail");

        params.put("applicationDetail", edcrApplicationDetail.getId());
        final Query query = getCurrentSession().createNativeQuery(queryApplnBldgDtls.toString());
        for (final Map.Entry<String, Long> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        Object[] applnBldgDtls = (Object[]) query.uniqueResult();
        PlanInformation pi = new PlanInformation();
        pi.setPlotArea(new BigDecimal(String.valueOf(applnBldgDtls[11])));
        pi.setOccupancy(String.valueOf(applnBldgDtls[1]));
        pi.setServiceType(String.valueOf(applnBldgDtls[2]));
        pi.setOwnerName(String.valueOf(applnBldgDtls[3]));
        pi.setArchitectInformation(String.valueOf(applnBldgDtls[4]));
        applicationInfo.setPlanDetailId(Long.valueOf(String.valueOf(applnBldgDtls[13])));
        Block block = new Block();
        block.setName("1");
        block.setNumber("1");
        Building bldg = new Building();
        bldg.setBuildingHeight(new BigDecimal(String.valueOf(applnBldgDtls[9])));
        bldg.setFloorsAboveGround(new BigDecimal(String.valueOf(applnBldgDtls[12])));
        final Map<String, Long> params1 = new HashMap<>();
        final StringBuilder queryForFloorDtls = new StringBuilder(1000);
        queryForFloorDtls.append(
                "select floor.name, meas.area from edcr_floor floor left outer join edcr_measurement meas on floor.id = meas.id "
                        + "  where floor.building=:builing");
        params1.put("builing", Long.valueOf(String.valueOf(applnBldgDtls[8])));
        final Query query1 = getCurrentSession().createNativeQuery(queryForFloorDtls.toString());
        for (final Map.Entry<String, Long> param : params1.entrySet())
            query1.setParameter(param.getKey(), param.getValue());
        List<Object[]> existFloors = query1.list();
        List<Floor> floors = new ArrayList<>();
        for (Object[] f : existFloors) {
            Floor floor = new Floor();
            floor.setNumber(Integer.valueOf(getFloorDescription(String.valueOf(f[0])).get(FLOOR_NO)));
            floor.setName(getFloorDescription(String.valueOf(f[0])).get(FLOOR_DESC));
            Occupancy occupancy = new Occupancy();
            occupancy.setType(OccupancyType.OCCUPANCY_A1);
            occupancy.setBuiltUpArea(new BigDecimal(String.valueOf(f[1])));
            occupancy.setFloorArea(new BigDecimal(String.valueOf(f[1])));
            occupancy.setCarpetArea(getPlinthArea(new BigDecimal(String.valueOf(f[1]))));
            List<Occupancy> occupancies = new ArrayList<>();
            occupancies.add(occupancy);
            floor.setOccupancies(occupancies);
            floors.add(floor);
        }
        bldg.setFloors(floors);
        BigDecimal totalBuiltUpArea = BigDecimal.ZERO;
        for (Floor f : floors)
            totalBuiltUpArea = totalBuiltUpArea.add(f.getOccupancies().get(0).getBuiltUpArea());
        bldg.setTotalBuitUpArea(totalBuiltUpArea);
        Occupancy occupancy1 = new Occupancy();
        occupancy1.setType(OccupancyType.OCCUPANCY_A1);
        occupancy1.setBuiltUpArea(totalBuiltUpArea);
        bldg.getTotalArea().add(occupancy1);
        block.setBuilding(bldg);
        Plan plan = new Plan();
        plan.setPlanInformation(pi);
        Occupancy o = new Occupancy();
        o.setType(OccupancyType.OCCUPANCY_A1);
        plan.getOccupancies().add(o);
        List<Block> blocks = new ArrayList<>();
        blocks.add(block);
        plan.setBlocks(blocks);
        applicationInfo.setPlan(plan);
    }

    private BigDecimal getPlinthArea(final BigDecimal floorArea) {
        return floorArea.multiply(new BigDecimal(80)).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
    }

    private Map<String, String> getFloorDescription(String floorName) {
        String floorNoStr = floorName.substring(floorName.lastIndexOf('_') + 1, floorName.length());
        Integer floorNo = Integer.valueOf(floorNoStr);
        Map<String, String> floorNoAndDesc = new ConcurrentHashMap<>();
        floorNoAndDesc.put(FLOOR_NO, floorNoStr);
        if (floorNo < 0)
            floorNoAndDesc.put(FLOOR_DESC, FloorDescription.CELLAR_FLOOR.getFloorDescriptionVal());
        else if (floorNo > 0)
            floorNoAndDesc.put(FLOOR_DESC, FloorDescription.UPPER_FLOOR.getFloorDescriptionVal());
        else
            floorNoAndDesc.put(FLOOR_DESC, FloorDescription.GROUND_FLOOR.getFloorDescriptionVal());
        return floorNoAndDesc;
    }
}
