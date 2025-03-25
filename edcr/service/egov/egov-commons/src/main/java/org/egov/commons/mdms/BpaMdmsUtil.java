
package org.egov.commons.mdms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
     */
    public void addMasterDetail(List<MasterDetail> bpaMasterDtls, String name) {
        MasterDetail masterDetail = new MasterDetail();
        masterDetail.setName(name);
        bpaMasterDtls.add(masterDetail);
    }


    public List<ModuleDetail> getBPAModuleRequest() {
        List<MasterDetail> bpaMasterDtls = new ArrayList<>();
        final String filterCode = "$.[?(@.active==true)].code";
        MasterDetail masterDetailAppType = new MasterDetail();
        masterDetailAppType.setName("ApplicationType");
        masterDetailAppType.setFilter(filterCode);
        bpaMasterDtls.add(masterDetailAppType);
        
        MasterDetail masterDetailServicetype = new MasterDetail();
        masterDetailServicetype.setName("ServiceType");
        masterDetailServicetype.setFilter(filterCode);
        bpaMasterDtls.add(masterDetailServicetype);
        
        MasterDetail masterDetailOccupancyType = new MasterDetail();
        masterDetailOccupancyType.setName("OccupancyType");
        masterDetailOccupancyType.setFilter("$.[?(@.active==true)]");
        bpaMasterDtls.add(masterDetailOccupancyType);
        
        MasterDetail masterDetailSubOccupancyType = new MasterDetail();
        masterDetailSubOccupancyType.setName("SubOccupancyType");
        masterDetailSubOccupancyType.setFilter("$.[?(@.active==true)]");
        bpaMasterDtls.add(masterDetailSubOccupancyType);

        MasterDetail masterDetailEdcrMdmsFeature = new MasterDetail();
        masterDetailEdcrMdmsFeature.setName("EdcrRulesFeatures");
        bpaMasterDtls.add(masterDetailEdcrMdmsFeature);
        
        MasterDetail masterDetailFar = new MasterDetail();
        masterDetailFar.setName("Far");
     //   masterDetailFar.setFilter("$.[?(@.active==true)]");
        bpaMasterDtls.add(masterDetailFar);
        
        MasterDetail masterDetailBalcony = new MasterDetail();
        masterDetailBalcony.setName("Balcony");
     //   masterDetailFar.setFilter("$.[?(@.active==true)]");
        bpaMasterDtls.add(masterDetailBalcony);
        
    
        // Adding different MasterDetail entries
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

        
        
        MasterDetail masterDetailToilet = new MasterDetail();
        masterDetailToilet.setName("Toilet");
        bpaMasterDtls.add(masterDetailToilet);
        
        MasterDetail masterDetailDoors = new MasterDetail();
        masterDetailDoors.setName("Doors");
        bpaMasterDtls.add(masterDetailDoors);
        
        MasterDetail masterDetailFrontSetBack = new MasterDetail();
        masterDetailFrontSetBack.setName("FrontSetBack");
        bpaMasterDtls.add(masterDetailFrontSetBack);
        
        MasterDetail masterDetailKitchen = new MasterDetail();
        masterDetailKitchen.setName("Kitchen");
        bpaMasterDtls.add(masterDetailKitchen);
        
        MasterDetail masterDetailLanding = new MasterDetail();
        masterDetailLanding.setName("Landing");
        bpaMasterDtls.add(masterDetailLanding);
        
        MasterDetail masterDetailLift = new MasterDetail();
        masterDetailLift.setName("Lift");
        bpaMasterDtls.add(masterDetailLift);
        
        MasterDetail masterDetailNonHabitationalDoors = new MasterDetail();
        masterDetailNonHabitationalDoors.setName("NonHabitationalDoors");
        bpaMasterDtls.add(masterDetailNonHabitationalDoors);
        
        MasterDetail masterDetailNoOfRiser = new MasterDetail();
        masterDetailNoOfRiser.setName("NoOfRiser");
        bpaMasterDtls.add(masterDetailNoOfRiser);
        
        MasterDetail masterDetailParking = new MasterDetail();
        masterDetailParking.setName("Parking");
        bpaMasterDtls.add(masterDetailParking);
        
        MasterDetail masterDetailPlantation = new MasterDetail();
        masterDetailPlantation.setName("Plantation");
        bpaMasterDtls.add(masterDetailPlantation);
        
        MasterDetail masterDetailPlintHeight = new MasterDetail();
        masterDetailPlintHeight.setName("PlinthHeight");
        bpaMasterDtls.add(masterDetailPlintHeight);
        
        MasterDetail masterDetailRearSetBack = new MasterDetail();
        masterDetailRearSetBack.setName("RearSetBack");
        bpaMasterDtls.add(masterDetailRearSetBack);
        
        MasterDetail masterDetailRequiredTread = new MasterDetail();
        masterDetailRequiredTread.setName("RequiredTread");
        bpaMasterDtls.add(masterDetailRequiredTread);
        
        MasterDetail masterDetailRequiredWidth = new MasterDetail();
        masterDetailRequiredWidth.setName("RequiredWidth");
        bpaMasterDtls.add(masterDetailRequiredWidth);
      
        MasterDetail masterDetailRiserHeight = new MasterDetail();
        masterDetailRiserHeight.setName("RiserHeight");
        bpaMasterDtls.add(masterDetailRiserHeight);
        
        MasterDetail masterDetailRoomArea = new MasterDetail();
        masterDetailRoomArea.setName("RoomArea");
        bpaMasterDtls.add(masterDetailRoomArea);
        
        MasterDetail masterDetailRoomWiseDoorArea = new MasterDetail();
        masterDetailRoomWiseDoorArea.setName("RoomWiseDoorArea");
        bpaMasterDtls.add(masterDetailRoomWiseDoorArea);
        
        MasterDetail masterDetailRoomWiseVentialtion = new MasterDetail();
        masterDetailRoomWiseVentialtion.setName("RoomWiseVentialtion");
        bpaMasterDtls.add(masterDetailRoomWiseVentialtion);
        
        MasterDetail masterDetailCoverage = new MasterDetail();
        masterDetailCoverage.setName("Coverage");
        bpaMasterDtls.add(masterDetailCoverage);
        
        MasterDetail masterDetailUsages = new MasterDetail();
        masterDetailUsages.setName("Usages");
        masterDetailUsages.setFilter("$.[?(@.active==true)]");
        bpaMasterDtls.add(masterDetailUsages);
        
        /*
         * MasterDetail masterDetailSubfeatureColorCode = new MasterDetail();
         * masterDetailSubfeatureColorCode.setName("SubFeatureColorCode"); masterDetailSubfeatureColorCode.setFilter("$.*");
         * bpaMasterDtls.add(masterDetailSubfeatureColorCode);
         */
        
        ModuleDetail bpaModuleDtls = new ModuleDetail();
        bpaModuleDtls.setMasterDetails(bpaMasterDtls);
        bpaModuleDtls.setModuleName("BPA");
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
