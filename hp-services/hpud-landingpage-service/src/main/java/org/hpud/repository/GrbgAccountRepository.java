package org.hpud.repository;

import java.util.List;

import org.hpud.entity.GrbgAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;


@Repository
public interface GrbgAccountRepository extends JpaRepository<GrbgAccount,Long>{

	@Query("SELECT grbgacc.id FROM GrbgAccount grbgacc WHERE grbgacc.mobile_number = :mobileNumber AND grbgacc.user_uuid !=:user_uuid OR grbgacc.user_uuid IS NULL")
	List<Long> getAccountForMobileNumber(@Param("mobileNumber") String mobileNumber,@Param("user_uuid") String user_uuid);
	
	@Modifying
	@Query("UPDATE GrbgAccount grbgacc SET grbgacc.user_uuid = :userUuid WHERE grbgacc.id IN :ids")
	int updateUserUuidByids(@Param("userUuid") String userUuid, @Param("ids") List<Long> ids);
}
