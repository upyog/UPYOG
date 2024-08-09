package digit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.BankBranch;
import digit.repository.BankBranchRepository;
import digit.web.models.SchemeApplicationRequest;

@Service
public class BankBranchService {
  
    private final  BankBranchRepository bankBranchRepository;

    @Autowired
    public BankBranchService(BankBranchRepository bankBranchRepository) {
        this.bankBranchRepository = bankBranchRepository;
    }

    public BankBranch getBankBranchByApplication(SchemeApplicationRequest schemeApplicationRequest) {

        BankBranch bankBranch = new BankBranch();
        bankBranch.setBranchcode(schemeApplicationRequest.getBranchcode());
        bankBranch.setBranchname(schemeApplicationRequest.getBranchname());
        bankBranch.setBranchaddress1(schemeApplicationRequest.getBranchaddress1());
        bankBranch.setBranchaddress2(schemeApplicationRequest.getBranchaddress2());
        bankBranch.setBranchcity(schemeApplicationRequest.getBranchcity());
        bankBranch.setBranchstate(schemeApplicationRequest.getBranchstate());
        bankBranch.setBranchpin(schemeApplicationRequest.getBranchpin());
        bankBranch.setBranchphone(schemeApplicationRequest.getBranchphone());
        bankBranch.setBranchfax(schemeApplicationRequest.getBranchfax());
        bankBranch.setBankid(schemeApplicationRequest.getBankid());
        bankBranch.setContactperson(schemeApplicationRequest.getContactperson());
        bankBranch.setIsactive(schemeApplicationRequest.getIsActive());
        bankBranch.setNarration(schemeApplicationRequest.getNarration());
        bankBranch.setMicr(schemeApplicationRequest.getMicr());
        bankBranch.setCreateddate(schemeApplicationRequest.getCreatedDate());
        bankBranch.setLastmodifieddate(schemeApplicationRequest.getLastModifiedDate());
        bankBranch.setLastmodifiedby(schemeApplicationRequest.getLastModifiedBy());
        bankBranch.setVersion(schemeApplicationRequest.getVersion());
        return  bankBranchRepository.save(bankBranch);
    }

    // public BankBranch getBankBranchByBranchCode (SchemeApplicationRequest schemeApplicationRequest ){

    //   return  bankBranchRepository.getBankBranchByBranchCode(schemeApplicationRequest.getBranchcode());
    // }

}
