package org.ksmart.birth.common.services;

import java.util.ArrayList;
import java.util.List;

import org.ksmart.birth.birth.model.ImportBirthWrapper;
import org.ksmart.birth.common.contract.BirthResponse;
import org.ksmart.birth.common.contract.DeathResponse;
import org.ksmart.birth.common.model.EgHospitalDtl;
import org.ksmart.birth.common.repository.CommonRepository;
import org.ksmart.birth.death.model.ImportDeathWrapper;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

	private CommonRepository repository;

	@Autowired
	public CommonService(CommonRepository repository) {
		this.repository = repository;
	}

	/**
	 * Search hospitals with tenantId
	 * @param tenantId
	 * @return list of hospitals
	 */
	public List<EgHospitalDtl> search(String tenantId) {
		List<EgHospitalDtl> hospitalDtls = new ArrayList<>() ;
		hospitalDtls = repository.getHospitalDtls(tenantId);
		return hospitalDtls;
	}

	/**
	 * saves birth details
	 * @param importJSon birth details
	 * @param requestInfo which consists of user details and auth token
	 * @return ImportBirthWrapper
	 */
	public ImportBirthWrapper saveBirthImport(BirthResponse importJSon, RequestInfo requestInfo) {
		return repository.saveBirthImport(importJSon, requestInfo);
	}

	/**
	 * saves death details
	 * @param importJSon death details
	 * @param requestInfo which consists of user details and auth token
	 * @return ImportDeathWrapper
	 */
	public ImportDeathWrapper saveDeathImport(DeathResponse importJSon, RequestInfo requestInfo) {
		return repository.saveDeathImport(importJSon, requestInfo);
	}

	/**
	 * updates birth details
	 * @param importJSon birth details
	 * @param requestInfo which consists of user details and auth token
	 * @return ImportBirthWrapper
	 */
	public ImportBirthWrapper updateBirthImport(BirthResponse importJSon, RequestInfo requestInfo) {
		return repository.updateBirthImport(importJSon, requestInfo);
	}

	/**
	 * updates death details
	 * @param importJSon death details
	 * @param requestInfo which consists of user details and auth token
	 * @return ImportDeathWrapper
	 */
	public ImportDeathWrapper updateDeathImport(DeathResponse importJSon, RequestInfo requestInfo) {
		return repository.updateDeathImport(importJSon, requestInfo);
	}

}
