package org.egov.finance.inbox.repository;

import java.util.Date;
import java.util.List;

import org.egov.finance.inbox.workflow.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
	 Long countByOwnerPositionAndCreatedDateGreaterThanEqual(Long id, Date givenDate);

	    @Query("select distinct s.type from State s where s.ownerPosition in (:ownerIds)  and s.status <> 2")
	    List<String> findAllTypeByOwnerAndStatus(@Param("ownerIds") List<Long> ownerIds);

	    @Query("select max(s.createdDate) from State s  where s.ownerPosition = :posId and s.status <> 2")
	    Date findMaxCreatedDateByOwnerPosId(@Param("posId") Long posId);
}
