package digit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.Bank;
import digit.repository.BankRepository;
import digit.web.models.SchemeApplicationRequest;

@Service
public class BankService {
    
    private final  BankRepository bankRepository;
    @Autowired
    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public  Bank getBankByApplication(SchemeApplicationRequest schemeApplicationRequest) {

        Bank bank = new Bank();
        bank.setName(schemeApplicationRequest.getName());
        bank.setCode(schemeApplicationRequest.getCode());
        bank.setId(schemeApplicationRequest.getBankid());
        bank.setIsActive(schemeApplicationRequest.getIsActive());
        bank.setNarration(schemeApplicationRequest.getNarration());
        bank.setType(schemeApplicationRequest.getType());
        bank.setVersion(schemeApplicationRequest.getVersion());
        return bankRepository.save(bank);
    }

    // public Bank getBankByCode (SchemeApplicationRequest schemeApplicationRequest){
    //     return  bankRepository.getBank(schemeApplicationRequest.getCode());
    // }


    // public List<Bank> fetchActiveBank (SchemeApplicationRequest schemeApplicationRequest) {

    //     return  bankRepository.fetchActiveBank(schemeApplicationRequest.getIsActive());
    // }
}
