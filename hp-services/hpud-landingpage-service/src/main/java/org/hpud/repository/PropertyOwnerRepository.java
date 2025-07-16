package org.hpud.repository;

import java.util.List;

import org.hpud.entity.PropertyOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyOwnerRepository extends JpaRepository<PropertyOwner,String> {
	
	
	@Query("SELECT ptowner.ownerinfouuid FROM PropertyOwner ptowner WHERE ptowner.mobile_number = :mobileNumber AND ptowner.userid !=:userid OR ptowner.userid IS NULL")
	List<String> getownerForMobileNumber(@Param("mobileNumber") String mobileNumber,@Param("userid") String userid);
	
	
	@Modifying
	@Query("UPDATE PropertyOwner ptowner SET ptowner.userid = :userUuid WHERE ptowner.ownerinfouuid IN :ids")
	int updateUserUuidByids(@Param("userUuid") String userUuid, @Param("ids") List<String> ids);
}
