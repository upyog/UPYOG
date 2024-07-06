package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import digit.bmc.model.UserOtherDetails;
@Repository
public interface UserOtherDetailsRepository extends  JpaRepository<UserOtherDetails, Long>{

}
