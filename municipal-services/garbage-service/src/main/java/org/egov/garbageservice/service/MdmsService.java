package org.egov.garbageservice.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.RestCallRepository;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Fetches garbage fee structure, penalty rate, and rebate rate from MDMS for a tenant.
 * Resolves charge amounts from master data during fee calculation and scheduler bill generation.
 */
@Service
@Slf4j
public class MdmsService {

    @Autowired
    RestCallRepository restCallRepository;

    @Autowired
    private GarbageServiceConfig config;

    /**
     * Constructs and executes an MDMS search query to retrieve the garbage fee structure.
     *
     * <p>This method builds an {@link MdmsCriteriaReq} containing the necessary module and master details.
     * It ensures the tenant ID is resolved to the state level before making the REST call to the MDMS service.
     *
     * @param requestInfo the contextual information for the API request
     * @param tenantId    the tenant ID for which the fee structure is requested
     * @return an {@link Object} containing the MDMS response payload
     */

    public Object fetchGarbageFeeFromMdms(RequestInfo requestInfo, String tenantId) {

        StringBuilder url = new StringBuilder(config.getMdmsServiceHostUrl()).append(config.getMdmsSearchEndpoint());

        List<ModuleDetail> moduleDetails = new ArrayList<>();
        List<MasterDetail> masterDetails = new ArrayList<>();

        masterDetails.add(MasterDetail.builder().name(GrbgConstants.MDMS_MASTER_NAME_FEE_STRUCTURE).build());
        moduleDetails.add(ModuleDetail.builder().moduleName(GrbgConstants.MDMS_MODULE_NAME_FEE_STRUCTURE)
                .masterDetails(masterDetails).build());

        // Fee structure master is state-level only; strip to state-level tenant for this MDMS lookup
        String stateLevelTenantId = tenantId.contains(".") ? tenantId.split("\\.")[0] : tenantId;

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(stateLevelTenantId).moduleDetails(moduleDetails).build();
        MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria)
                .build();

        Object mdmsResponse = restCallRepository.fetchResult(url, mdmsCriteriaReq);

        return mdmsResponse;
    }

    /**
     * Parses the MDMS response to extract the specific garbage fee amount for a given account.
     *
     * <p>The extraction process performs the following steps:
     * <ol>
     *   <li>Reads the fee structure list from the MDMS response using JSONPath.</li>
     *   <li>Iterates through the fee categories to find a match with the account's collection unit category.</li>
     *   <li>If a match is found, captures the fee amount, service type, and fee type into the calculation breakdown.</li>
     *   <li>Populates the error list if no matching category is found in the fee structure.</li>
     * </ol>
     *
     * @param mdmsResponse         the raw response object obtained from the MDMS service
     * @param garbageAccount       the garbage account containing the target collection unit category
     * @param errorList            a list to which error messages are appended if validation fails
     * @param calculationBreakdown an {@link ObjectNode} populated with the fee details for auditing and response
     * @return the matched fee amount as a {@link BigDecimal}
     */

    public BigDecimal fetchGarbageAmountFromMDMSResponse(Object mdmsResponse, GarbageAccount garbageAccount,
                                                         List<String> errorList, ObjectNode calculationBreakdown) {

        AtomicReference<BigDecimal> taxAmount = new AtomicReference<>(null);
        List<LinkedHashMap<Object, Object>> feeStructureList = JsonPath.read(
                mdmsResponse, "$.MdmsRes.Garbage.CalculationType");

        feeStructureList.forEach(obj -> {
            Object categoryObj = obj.get("categories");
            Object amountObj = obj.get("amount");

            if (categoryObj == null || amountObj == null) {
                return;
            }

            if (!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())
                    && categoryObj.toString().equalsIgnoreCase(
                    garbageAccount.getGrbgCollectionUnits().get(0).getCategory())) {

                BigDecimal fee = BigDecimal.valueOf(Double.parseDouble(amountObj.toString()));
                calculationBreakdown.put("fee", fee.toString());
                calculationBreakdown.put("serviceType", String.valueOf(obj.get("serviceType")));
                calculationBreakdown.put("feeType", String.valueOf(obj.get("feeType")));

                taxAmount.set(fee);
            }
        });

        if (taxAmount.get() == null) {
            errorList.add("Category mismatch");
        } else {
            calculationBreakdown.put("final_amount", taxAmount.get().toString());
        }
        return taxAmount.get();
    }
}