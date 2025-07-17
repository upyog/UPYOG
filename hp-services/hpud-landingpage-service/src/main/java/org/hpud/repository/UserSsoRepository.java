package org.hpud.repository;

import java.util.List;

import org.hpud.entity.UserSso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSsoRepository extends JpaRepository<UserSso, Long> {
//	@Query("SELECT count(*) from LandingPage")
//	int countTotal();
	
	@Query("SELECT u.user_uuid FROM UserSso u WHERE u.user_uuid IN :userUuids")
	List<String> findSssoMappedUser(@Param("userUuids") List<String> userUuids);


}
