package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import digit.bmc.model.Caste;
@Repository
public interface CasteRepository extends  JpaRepository<Caste, Long>{

}
