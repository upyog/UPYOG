package digit.service;

import java.sql.Timestamp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import digit.bmc.model.Bank;
import digit.bmc.model.BankBranch;
import digit.repository.BankRepository;
import digit.web.models.BankDetails;
import digit.web.models.bank.BankSearchCriteria;
import digit.web.models.bank.RazorPayBankDetails;


@Service
public class BankService {
    
    @Value("${razorpay.url}")
    private String razorPayUrl;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    BankBranchService branchService;
    private final  BankRepository bankRepository;
    @Autowired
    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

  

      public List<BankDetails> getBankDetails(BankSearchCriteria criteria) {
        List<BankDetails> details = bankRepository.getCommonDetails(criteria);
        return details;
    }

    public RazorPayBankDetails getBankDetailsFromRazorPay(String ifscCode) {
        String url = razorPayUrl + ifscCode;
        RazorPayBankDetails details =  restTemplate.getForObject(url, RazorPayBankDetails.class);
        BankBranch branch = new BankBranch();
        Long bankId = bankRepository.getBankId(details.getBankCode());
        if(bankId != null){
            branch.setBankid(bankId);
        }
        else{
            Bank bank = new Bank();
            bank.setCode(details.getBankCode());
            bank.setName(details.getBank());
            bank.setIsActive(true);
            bank.setNarration(null);
            bank.setType(null);
            bankId = bankRepository.insertBank(bank);
            branch.setBankid(bankId);
        }
        String add = truncateString(details.getAddress(),49);
        branch.setBranchaddress1(add);  //to increase the size from 50 to 255
        branch.setBranchaddress2(details.getDistrict());
        branch.setBranchcity(details.getCity());
        branch.setBranchstate(details.getState());
        branch.setMicr(details.getMicr());
        branch.setIfsc(details.getIfsc());
        branch.setBranchphone(details.getContact());
        branch.setIsactive(true);
        branch.setBranchname(details.getBranch());
        branch.setBranchpin(extractPinCode(details.getAddress()));
        branch.setBranchcode(details.getIfsc().substring(4));
        BankBranch bb = branchService.saveBankBranch(branch);
        details.setBranchId(bb.getId());
        return details;
    }
    public static String extractPinCode(String address) {
        
        String pinCodePattern = "\\b\\d{6}\\b";
        Pattern pattern = Pattern.compile(pinCodePattern);
        Matcher matcher = pattern.matcher(address);
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    private String truncateString(String value, int length) {
        if (value != null && value.length() > length) {
            return value.substring(0, length);
        }
        return value;
    }

}
