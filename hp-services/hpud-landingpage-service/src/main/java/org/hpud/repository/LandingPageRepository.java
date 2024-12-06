package org.hpud.repository;

import org.hpud.entity.LandingPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LandingPageRepository extends JpaRepository<LandingPage, Long> {
	@Query("SELECT count(*) from LandingPage")
	int countTotal();
}
