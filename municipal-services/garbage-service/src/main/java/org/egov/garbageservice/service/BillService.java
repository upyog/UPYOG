package org.egov.garbageservice.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.contract.bill.*;
import org.egov.garbageservice.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Facade over BillRepository for collection-service bill fetch, search, update, and cancel.
 * Builds BillResponse with ResponseInfo and delegates HTTP calls to the billing microservice.
 */
@Service
@Slf4j
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;


    /**
     * Delegates to the billing microservice to generate a fresh bill payload.
     *
     * <p>This operation fetches the latest calculated demand against a specific garbage
     * account and packages it into a unified bill structure.
     *
     * @param requestInfo  the contextual information for the API request
     * @param billCriteria the search criteria used to target specific billing data
     * @return a {@link BillResponse} containing the generated bill details
     */

    BillResponse generateBill(RequestInfo requestInfo, GenerateBillCriteria billCriteria) {
        BillResponse billResponse = billRepository.fetchBill(billCriteria, requestInfo);
        return billResponse;
    }

    /**
     * Searches the bills from DB for given criteria and enriches them with TaxAndPayments array
     *
     * @param requestInfo
     * @return
     */
    public BillResponse searchBill(BillSearchCriteria billSearchCriteria, RequestInfo requestInfo) {
        List<Bill> bills = billRepository.searchBill(billSearchCriteria, requestInfo);
        return BillResponse.builder().resposneInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true))
                .bill(bills).build();
    }



    /**
     * Updates an existing bill record through the billing repository.
     *
     * <p>This ensures that any modifications to the underlying demands (such as partial
     * payments or applied penalties) are properly reflected in the billing subsystem.
     *
     * @param requestInfo the contextual information for the API request
     * @param bills       the updated {@link Bill} list to be saved
     */

    public void updateBill(RequestInfo requestInfo, List<Bill> bills) {
        billRepository.updateBill(requestInfo, bills);
    }


}
