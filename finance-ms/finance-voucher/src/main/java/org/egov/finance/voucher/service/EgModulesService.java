package org.egov.finance.voucher.service;

import java.util.List;

import org.egov.finance.voucher.model.EgModules;
import org.egov.finance.voucher.repository.EgModulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EgModulesService {

	@Autowired
	EgModulesRepository egModuleRepository;

	public List<EgModules> getEgModuleServiceByName(String name) {
		return egModuleRepository.findEgModulesByName(name);
	}

}
