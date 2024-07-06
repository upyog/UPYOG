package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import digit.web.models.Ward;

@Repository
public interface WardRepository extends  JpaRepository<Ward, Long>{

}
