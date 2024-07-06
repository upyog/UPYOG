package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import digit.web.models.BmcUser;

@Repository
public interface  BmcUserRepository extends JpaRepository<BmcUser,Long>{


}
