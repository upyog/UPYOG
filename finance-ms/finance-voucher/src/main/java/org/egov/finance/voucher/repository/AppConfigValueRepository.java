package org.egov.finance.voucher.repository;

import java.util.Date;
import java.util.List;

import org.egov.finance.voucher.entity.AppConfigValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigValueRepository extends JpaRepository<AppConfigValues, Long> {

	List<AppConfigValues> findByConfig_KeyNameAndConfig_Module_Name(String keyName, String moduleName);

	List<AppConfigValues> findByConfig_KeyNameLikeAndConfig_Module_Name(String keyName, String moduleName);

	List<AppConfigValues> findByConfig_KeyNameAndConfig_Module_NameOrderByValueAsc(String keyName, String moduleName);

	@Query("select a from AppConfigValues  a where a.config.keyName =:keyName and a.config.module.name =:moduleName and (a.effectiveFrom < :effectiveFrom or a.effectiveFrom between :dateFrom and :dateTo) order by effectiveFrom asc")
	List<AppConfigValues> getAppConfigValueByModuleAndKeyAndDate(@Param("moduleName") String moduleName,
			@Param("keyName") String keyName, @Param("effectiveFrom") Date effectiveFrom,
			@Param("dateFrom") Date fromDate, @Param("dateTo") Date toDate);

}
