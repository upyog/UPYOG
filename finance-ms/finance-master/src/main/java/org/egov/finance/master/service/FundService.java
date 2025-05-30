/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.master.service;

import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.repository.FundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class FundService {

	@Autowired
	private  FundRepository fundRepository;
	
	@Transactional
	public Fund findOne(final Long id) {
		
		return fundRepository.findById(id).orElse(null);
	}

	
}

