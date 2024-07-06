package digit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import digit.bmc.model.BankAccount;
@EnableJpaRepositories
public interface BankAccountRepository extends JpaRepository<BankAccount,Long>{

    List<BankAccount> getByAccountNumber (String accountNumber);

}
