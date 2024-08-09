package digit.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.BankAccount;
import digit.repository.BankAccountRepository;
import digit.web.models.SchemeApplicationRequest;

@Service
public class BankAccountService {
   @Autowired
    private BankAccountRepository bankAccountRepository;


    public BankAccount getBankAccountByApplication(SchemeApplicationRequest schemeApplicationRequest){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBranchId(schemeApplicationRequest.getBranchId());
        bankAccount.setAccountNumber(schemeApplicationRequest.getAccountNumber());
        bankAccount.setAccountType(schemeApplicationRequest.getAccountType());
        bankAccount.setNarration(schemeApplicationRequest.getNarration());
        bankAccount.setIsActive(schemeApplicationRequest.getIsActive());
        bankAccount.setPayTo(schemeApplicationRequest.getPayTo());
        bankAccount.setType(schemeApplicationRequest.getType());
        bankAccount.setLastModifiedBy(schemeApplicationRequest.getLastModifiedBy());
        bankAccount.setCreatedDate(schemeApplicationRequest.getCreatedDate());
        bankAccount.setLastModifiedDate(schemeApplicationRequest.getLastModifiedDate());
        bankAccount.setVersion(schemeApplicationRequest.getVersion());
        bankAccount.setChequeFormatId(schemeApplicationRequest.getChequeFormatId());

        return  bankAccountRepository.save(bankAccount);
    }

    
    

    public Map<String, List<String>> getDropdownValues() {
        Map<String, List<String>> dropdownValues = new HashMap<>();
        BankAccount bankAccount = new BankAccount();
        List<String> accountTypes = new ArrayList<>();
        accountTypes.add(bankAccount.getAccountType());
        List<String> accountNumbers = new ArrayList<>();
        accountNumbers.add(bankAccount.getAccountNumber());    
        dropdownValues.put("AccountType", accountTypes);
        dropdownValues.put("AccountNumber", accountNumbers);

        return dropdownValues;
    }

    public List<BankAccount> getBankAccountList (SchemeApplicationRequest schemeApplicationRequest){

        return bankAccountRepository.getByAccountNumber(schemeApplicationRequest.getAccountNumber());
    }
}


   
