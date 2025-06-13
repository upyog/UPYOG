package org.upyog.tp.util;
import java.util.ArrayList;
import java.util.List;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.constant.TreePruningConstants;
import org.upyog.tp.repository.ServiceRequestRepository;
import org.upyog.tp.web.models.billing.CalculationType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Slf4j
@Component
public class MdmsUtil {

    @Autowired
    private ServiceRequestRepository restRepo;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TreePruningConfiguration config;

    public List<CalculationType> getCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

        List<CalculationType> calculationTypes = new ArrayList<CalculationType>();
        StringBuilder uri = new StringBuilder();
        uri.append(config.getMdmsHost()).append(config.getMdmsEndpoint());

        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, moduleName);
        MdmsResponse mdmsResponse = mapper.convertValue(restRepo.fetchResult(uri, mdmsCriteriaReq), MdmsResponse.class);

        if (mdmsResponse.getMdmsRes().get(TreePruningConstants.MDMS_MODULE_NAME) == null) {
            throw new CustomException("FEE_NOT_AVAILABLE", "Tree Pruning fee not available.");
        }
        JSONArray jsonArray = mdmsResponse.getMdmsRes().get(TreePruningConstants.MDMS_MODULE_NAME)
                .get(TreePruningConstants.MDMS_TREE_PRUNING_CALCULATION_TYPE);

        try {
            calculationTypes = mapper.readValue(jsonArray.toJSONString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
        } catch (JsonProcessingException e) {
            log.info("Exception occured while converting calculation type  for tree pruning request: " + e);
        }

        return calculationTypes;

    }

    private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

        MasterDetail masterDetail = new MasterDetail();
        masterDetail.setName(TreePruningConstants.MDMS_TREE_PRUNING_CALCULATION_TYPE);
        List<MasterDetail> masterDetailList = new ArrayList<>();
        masterDetailList.add(masterDetail);

        ModuleDetail moduleDetail = new ModuleDetail();
        moduleDetail.setMasterDetails(masterDetailList);
        moduleDetail.setModuleName(moduleName);
        List<ModuleDetail> moduleDetailList = new ArrayList<>();
        moduleDetailList.add(moduleDetail);

        MdmsCriteria mdmsCriteria = new MdmsCriteria();
        mdmsCriteria.setTenantId(tenantId);
        mdmsCriteria.setModuleDetails(moduleDetailList);

        MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
        mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
        mdmsCriteriaReq.setRequestInfo(requestInfo);

        return mdmsCriteriaReq;
    }

}