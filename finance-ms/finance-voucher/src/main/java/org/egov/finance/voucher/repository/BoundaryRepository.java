package org.egov.finance.voucher.repository;

import java.util.List;

import org.egov.finance.voucher.entity.Boundary;
import org.egov.finance.voucher.entity.BoundaryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoundaryRepository extends JpaRepository<Boundary, Long> {

	List<Boundary> findByBoundaryTypeOrderByBoundaryNumAsc(BoundaryType boundaryType);

	List<Boundary> findByBoundaryTypeId(Long boundaryTypeId);

	@Query("from Boundary BND where BND.active = true AND BND.parent.id = :parentId")
	List<Boundary> findActiveImmediateChildrenWithOutParent(@Param("parentId") Long parentId);

	@Query("from Boundary BND where BND.parent is null")
	List<Boundary> findAllParents();
}
