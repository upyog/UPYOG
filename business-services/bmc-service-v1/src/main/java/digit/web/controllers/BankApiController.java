package digit.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import digit.service.BankService;
import digit.util.ResponseInfoFactory;
import digit.web.models.BankDetails;
import digit.web.models.bank.BankSearchRequest;
import digit.web.models.bank.BankSearchResponse;
import digit.web.models.bank.RazorPayBankDetails;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/bank")
public class BankApiController {

    @Autowired
    private BankService bankService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;
    

    @PostMapping("/_get")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<BankSearchResponse> getBankDetails(@ApiParam(value = "Bank Details", required = true) @RequestBody BankSearchRequest searchRequest) {

        try {
            
            if (searchRequest.getBankSearchCriteria() == null ||
                    StringUtils.isEmpty(searchRequest.getBankSearchCriteria().getIfsc())) {
                return new ResponseEntity<>(null, HttpStatus.OK); 
            }
           List<BankDetails> bankDetails = bankService.getBankDetails(searchRequest.getBankSearchCriteria());
            if(ObjectUtils.isEmpty(bankDetails)){
                RazorPayBankDetails razorPayBankDetails = bankService.getBankDetailsFromRazorPay(searchRequest.getBankSearchCriteria().getIfsc());
                if (razorPayBankDetails != null) {
                    bankDetails = new ArrayList<>();
                    BankDetails details = new BankDetails();
                    details.setBranchId(razorPayBankDetails.getBranchId());
                    details.setBranchName(razorPayBankDetails.getBranch());
                    details.setName(razorPayBankDetails.getBank());
                    details.setIfsc(razorPayBankDetails.getIfsc());
                    details.setMicr(razorPayBankDetails.getMicr());
                    bankDetails.add(details);
                }
            }
            ResponseInfo responseInfo = responseInfoFactory
                       .createResponseInfoFromRequestInfo(searchRequest.getRequestInfo(), true);
            BankSearchResponse response = BankSearchResponse.builder()
                .bankDetails(bankDetails)
                .responseInfo(responseInfo)
                .build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseInfo responseInfo = responseInfoFactory
            .createResponseInfoFromRequestInfo(searchRequest.getRequestInfo(), false);
            return new ResponseEntity<>(new BankSearchResponse(responseInfo, null,"Invalid ifsc code"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

