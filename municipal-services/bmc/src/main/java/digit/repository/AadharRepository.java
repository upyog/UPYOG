package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import digit.bmc.model.AadharUser;

@EnableJpaRepositories
public interface AadharRepository extends  JpaRepository<AadharUser, Long>{

}
