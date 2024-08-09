package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import digit.web.models.Religion;

@Repository
public interface ReligionRepositoty extends  JpaRepository<Religion, Long>{

}
