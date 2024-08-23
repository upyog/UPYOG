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

    public BankBranch saveBankBranch(BankBranch branch){
       return bankBranchRepository.save(branch) ;
    }

}
