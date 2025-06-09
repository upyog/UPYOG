/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.service;

import org.egov.finance.master.repository.FunctionRepository;
import org.egov.finance.master.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FunctionService {
	@Autowired
	FunctionRepository functionRepo;
	@Autowired
	private CacheEvictionService cacheEvictionService;
	@Autowired
	private CommonUtils commonUtils;
	

}

