package org.egov.commons.repository;


import java.math.BigInteger;

import java.math.BigInteger;

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

	
	@Query(value = "SELECT VH.id AS MissingVoucherHeaders " +
            "FROM citya.voucherheader VH " +
            "LEFT JOIN citya.generalledger GL ON VH.Id = GL.voucherheaderid " +
            "WHERE GL.voucherheaderid IS NULL",
     nativeQuery = true)
List<BigInteger> findMissingVoucherHeaders();

}


