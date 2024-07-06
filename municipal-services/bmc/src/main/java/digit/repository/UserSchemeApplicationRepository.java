package digit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import digit.bmc.model.UserSchemeApplication;


@Repository
public interface UserSchemeApplicationRepository extends JpaRepository<UserSchemeApplication,Long>{
    Optional<UserSchemeApplication> findByApplicationNumber(String applicationNumber);
}
