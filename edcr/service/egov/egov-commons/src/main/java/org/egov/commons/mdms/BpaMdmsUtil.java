
package org.egov.commons.mdms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.commons.mdms.config.MdmsConfiguration;
import org.egov.commons.mdms.model.MasterDetail;
import org.egov.commons.mdms.model.MdmsCriteria;
import org.egov.commons.mdms.model.MdmsCriteriaReq;
import org.egov.commons.mdms.model.ModuleDetail;
import org.egov.commons.service.RestCallService;
import org.egov.infra.microservice.models.RequestInfo;
import org.springframework.stereotype.Service;

@Service
public class BpaMdmsUtil {
    private RestCallService serviceRequestRepository;
    private MdmsConfiguration mdmsConfiguration;

    public BpaMdmsUtil(RestCallService serviceRequestRepository, MdmsConfiguration mdmsConfiguration) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.mdmsConfiguration = mdmsConfiguration;
    }
    
    
    /**
     * Adds a new MasterDetail object to the given list with the specified name.
     *
     * @param bpaMasterDtls The list where the MasterDetail object will be added.
     * @param name The name to be assigned to the MasterDetail object.
     * @author khalid-rashid
     */
    public void addMasterDetail(List<MasterDetail> bpaMasterDtls, String name) {
        MasterDetail masterDetail = new MasterDetail();
        masterDetail.setName(name);
        bpaMasterDtls.add(masterDetail);
    }


    public List<ModuleDetail> getBPAModuleRequest() {
        List<MasterDetail> bpaMasterDtls = new ArrayList<>();
        final String filterCode = "$.[?(@.active==true)].code";
    
        // Adding different MasterDetail entries
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.TOILET);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.DOORS);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.FRONT_SETBACK);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.KITCHEN);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.LANDING);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.LIFT);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.NON_HABITATIONAL_DOORS);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.NO_OF_RISER);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.PARKING);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.PLANTATION);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.PLINTH_HEIGHT);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.REAR_SETBACK);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.REQUIRED_TREAD);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.REQUIRED_WIDTH);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.RISER_HEIGHT);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.ROOM_AREA);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.ROOM_WISE_DOOR_AREA);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.ROOM_WISE_VENTILATION);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.COVERAGE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.USAGES);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.APPLICATION_TYPE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.SERVICE_TYPE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.OCCUPANCY_TYPE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.SUB_OCCUPANCY_TYPE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.EDCR_RULES_FEATURES);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.FAR);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.BALCONY);

        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.BASEMENT);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.BATHROOM);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.BATHROOM_WATER_CLOSETS);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.BLOCK_DISTANCES_SERVICE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.CHIMNEY);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.EXIT_WIDTH);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.FIRE_STAIR);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.FIRE_TENDER_MOVEMENT);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.GOVT_BUILDING_DISTANCE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.GUARD_ROOM);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.HEAD_ROOM);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.LAND_USE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.INTERIOR_OPEN_SPACE_SERVICE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.MEZZANINE_FLOOR_SERVICE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.MONUMENT_DISTANCE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.OVERHEAD_ELECTRICAL_LINE_SERVICE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.OVERHANGS);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.PARAPET);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.PASSAGE_SERVICE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.PLANTATION_GREEN_STRIP);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.PLOT_AREA);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.PORTICO_SERVICE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.RAIN_WATER_HARVESTING);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.RAMP_SERVICE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.RIVER_DISTANCE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.ROAD_WIDTH);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.ROOF_TANK);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.SEGREGATED_TOILET);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.SEPTIC_TANK);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.SOLAR);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.SPIRAL_STAIR);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.STAIR_COVER);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.STORE_ROOM);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.TERRACE_UTILITY_SERVICE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.TRAVEL_DISTANCE_TO_EXIT);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.VEHICLE_RAMP);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.VENTILATION);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.VERANDAH);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.WATER_CLOSETS);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.WATER_TANK_CAPACITY);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.SANITATION);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.SIDE_YARD_SERVICE);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.RISK_TYPE_COMPUTATION);
        addMasterDetail(bpaMasterDtls, MdmsFeatureConstants.ADDITIONAL_FEATURE);

        ModuleDetail bpaModuleDtls = new ModuleDetail();
        bpaModuleDtls.setMasterDetails(bpaMasterDtls);
        bpaModuleDtls.setModuleName(MdmsFeatureConstants.BPA);
        return Arrays.asList(bpaModuleDtls);
    }

    private MdmsCriteriaReq getBpaMDMSRequest(RequestInfo requestInfo, String tenantId) {
        List<ModuleDetail> moduleRequest = getBPAModuleRequest();
        List<ModuleDetail> moduleDetails = new LinkedList<>();
        moduleDetails.addAll(moduleRequest);
        MdmsCriteria mdmsCriteria = new MdmsCriteria();
        mdmsCriteria.setModuleDetails(moduleDetails);
        mdmsCriteria.setTenantId(tenantId);
        MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
        mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
        mdmsCriteriaReq.setRequestInfo(requestInfo);
        return mdmsCriteriaReq;
    }

    public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
        MdmsCriteriaReq mdmsCriteriaReq = getBpaMDMSRequest(requestInfo,
                tenantId);
        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
        return result;
    }
    
    public Object mDMSCallv2(RequestInfo requestInfo, String tenantId) {
        MdmsCriteriaReq mdmsCriteriaReq = getBpaMDMSRequest(requestInfo,
                tenantId);
        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrlv2(), mdmsCriteriaReq);
        return result;
    }

    public StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(mdmsConfiguration.getMdmsHost()).append(mdmsConfiguration.getMdmsSearchUrl());
    }
    
    public StringBuilder getMdmsSearchUrlv2() {
        return new StringBuilder().append(mdmsConfiguration.getMdmsHost()).append(mdmsConfiguration.getMdmsSearchUrlv2());
    }
}
