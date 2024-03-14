package org.egov.commons.repository;

<<<<<<< HEAD
<<<<<<< HEAD
import java.math.BigInteger;
=======
>>>>>>> 8d2ef484acce46e0f39d80188c2b43ccca9e9508
=======
import java.math.BigInteger;
>>>>>>> 6d75aca06005806a5dce3b5f6e818fd3149a7977
import java.util.List;
import java.util.Optional;

import org.egov.commons.CVoucherHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CVoucherHeaderRepository  extends JpaRepository<CVoucherHeader,Long>{
	
	List<CVoucherHeader> findAll();
	
	@Query(value = "SELECT * FROM citya.voucherheader WHERE id = (SELECT MAX(id) FROM citya.voucherheader)", nativeQuery = true)
    Optional<CVoucherHeader> getDetailsForMaxIdFromVoucherHeader();
	
	@Query(value = "SELECT * FROM citya.voucherheader WHERE id = (SELECT MAX(id) FROM citya.voucherheader)", nativeQuery = true)
    public  CVoucherHeader getDetailsForMaxIdFromVoucherHeaders();
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 6d75aca06005806a5dce3b5f6e818fd3149a7977
	
	
	@Query(value = "SELECT VH.id AS MissingVoucherHeaders " +
            "FROM citya.voucherheader VH " +
            "LEFT JOIN citya.generalledger GL ON VH.Id = GL.voucherheaderid " +
            "WHERE GL.voucherheaderid IS NULL",
     nativeQuery = true)
List<BigInteger> findMissingVoucherHeaders();
<<<<<<< HEAD
=======
>>>>>>> 8d2ef484acce46e0f39d80188c2b43ccca9e9508
=======
>>>>>>> 6d75aca06005806a5dce3b5f6e818fd3149a7977
}


