package org.egov.swservice.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.swservice.web.models.SearchCriteria;
import org.egov.swservice.web.models.SewerageConnection;
import org.egov.swservice.web.models.SewerageConnectionRequest;

public interface SewerageService {

	List<SewerageConnection> createSewerageConnection(SewerageConnectionRequest sewarageConnectionRequest);
	
	List<SewerageConnection> createSewerageConnection(SewerageConnectionRequest sewarageConnectionRequest, Boolean isMigration);

	List<SewerageConnection> search(SearchCriteria criteria, RequestInfo requestInfo);
	
	List<SewerageConnection> searchSewerageConnectionPlainSearch(SearchCriteria criteria, RequestInfo requestInfo);
	
	void disConnectSewerageConnection(String connectionNo,RequestInfo requestInfo,String tenantId);

	Integer countAllSewerageApplications(SearchCriteria criteria, RequestInfo requestInfo);
	
	List<SewerageConnection> updateSewerageConnection(SewerageConnectionRequest sewarageConnectionRequest);
	
	List<SewerageConnection> plainSearch(SearchCriteria criteria, RequestInfo requestInfo);

}
