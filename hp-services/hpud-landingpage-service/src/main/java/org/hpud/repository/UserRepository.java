package org.hpud.repository;

import java.util.List;

import org.hpud.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//	@Query("SELECT count(*) from LandingPage")
//	int countTotal();
//	@Query(value="SELECT u.mobileNumber FROM User u WHERE u.active=true GROUP BY u.mobileNumber HAVING COUNT(u) > 1 LIMIT :limit", nativeQuery = true)

	
	@Query(value = "SELECT mobilenumber FROM eg_user WHERE active=true AND type='CITIZEN' GROUP BY mobilenumber HAVING COUNT(*) > 1 LIMIT :limit", nativeQuery = true)
	List<String> getDuplicateMobiles(@Param("limit") int limit);
	
	@Query("SELECT u.uuid FROM User u WHERE u.mobileNumber = :mobileNumber AND u.type = 'CITIZEN' AND u.active=true")
	List<String> getUuidForMobileNumber(@Param("mobileNumber") String mobileNumber);
	
	@Modifying
	@Query("UPDATE User u SET u.active = :active WHERE u.uuid IN :ids")
	int toggleUserActivity(@Param("active") Boolean active, @Param("ids") List<String> ids);


}
