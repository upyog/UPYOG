package org.egov.egf.commons.bank.service;

import java.util.List;

import org.egov.commons.StateMaster;
import org.egov.egf.commons.bank.repository.BankRepository;
import org.egov.egf.commons.bank.repository.StateMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author shahrukk
 */
@Service
@Transactional(readOnly = true)
public class StateMasterService {
	
	private final StateMasterRepository stateMasterRepository;
	
	@Autowired
	public StateMasterService(final StateMasterRepository stateMasterRepository) {
		this.stateMasterRepository = stateMasterRepository;
	}

	public List<StateMaster> getAllState() {
		return stateMasterRepository.findAll();
	}

}
