package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import digit.bmc.model.Bank;
@EnableJpaRepositories
@Repository
public interface BankRepository extends JpaRepository<Bank,Long>{

   // Bank getBank(String code);

    //List<Bank> fetchActiveBank (Boolean isActive);

}
