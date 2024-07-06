package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import digit.bmc.model.Divyang;
@Repository
public interface DivyangRepository extends  JpaRepository<Divyang, Long>{

}
