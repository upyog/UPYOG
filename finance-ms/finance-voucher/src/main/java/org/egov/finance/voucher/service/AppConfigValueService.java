package org.egov.finance.voucher.service;

import java.util.Date;
import java.util.List;

import org.egov.finance.voucher.entity.AppConfigValues;
import org.egov.finance.voucher.repository.AppConfigValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AppConfigValueService {

	private final AppConfigValueRepository appConfigValueRepository;

	@Autowired
	public AppConfigValueService(final AppConfigValueRepository appConfigValueRepos) {
		appConfigValueRepository = appConfigValueRepos;
	}

	public AppConfigValues getById(Long id) {
		return appConfigValueRepository.getOne(id);
	}

	public List<AppConfigValues> getConfigValuesByModuleAndKey(String moduleName, String keyName) {
		return appConfigValueRepository.findByConfig_KeyNameAndConfig_Module_Name(keyName, moduleName);
	}

	public List<AppConfigValues> getConfigValuesByModuleAndKeyLike(String moduleName, String keyName) {
		return appConfigValueRepository.findByConfig_KeyNameLikeAndConfig_Module_Name(keyName, moduleName);
	}

	public List<AppConfigValues> getConfigValuesByModuleAndKeyByValueAsc(String moduleName, String keyName) {
		return appConfigValueRepository.findByConfig_KeyNameAndConfig_Module_NameOrderByValueAsc(keyName, moduleName);
	}

//	public AppConfigValues getAppConfigValueByDate(String moduleName, String keyName, Date effectiveFrom) {
//		List<AppConfigValues> appConfigValues = appConfigValueRepository.getAppConfigValueByModuleAndKeyAndDate(
//				moduleName, keyName, effectiveFrom, startOfDay(effectiveFrom), endOfDay(effectiveFrom));
//		return appConfigValues.isEmpty() ? null : appConfigValues.get(appConfigValues.size() - 1);
//	}
//
//	public String getAppConfigValue(String moduleName, String keyName, String defaultVal) {
//		List<AppConfigValues> appConfigValues = appConfigValueRepository.getAppConfigValueByModuleAndKeyAndDate(
//				moduleName, keyName, now(), startOfToday().toDate(), endOfToday().toDate());
//		return appConfigValues.isEmpty() ? defaultVal : appConfigValues.get(appConfigValues.size() - 1).toString();
//
//	}

}
