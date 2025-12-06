package org.egov.pqm.anomaly.finder.service;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pqm.anomaly.finder.config.PqmAnomalyConfiguration;
import org.egov.pqm.anomaly.finder.repository.AnomalyRepository;
import org.egov.pqm.anomaly.finder.service.notification.NotificationService;
import org.egov.pqm.anomaly.finder.validator.TestAnomalyFinderValidator;
import org.egov.pqm.anomaly.finder.web.model.PqmAnomaly;
import org.egov.pqm.anomaly.finder.web.model.PqmAnomalyRequest;
import org.egov.pqm.anomaly.finder.web.model.PqmAnomalySearchCriteria;
import org.egov.pqm.anomaly.finder.web.model.TestRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnomalyFinderService {

	@Autowired
	private AnomalyRepository anomalyRepository;

	@Autowired
	private TestAnomalyFinderValidator testAnomalyFinderValidator;

	@Autowired
	private EnrichmentService enrichmentService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private PqmAnomalyConfiguration config;

	public void anomalyCreate(TestRequest testRequest, String topic) {

		TestRequest testRequests = testAnomalyFinderValidator.validateCreate(testRequest);
		PqmAnomalyRequest pqmAnomalyRequest = enrichmentService.enrichPqmAnomalyCreateRequest(testRequests, topic);
		anomalyRepository.save(pqmAnomalyRequest);
		notificationService.process(testRequests,topic);
	}
	
	public List<PqmAnomaly> pqmAnomalyPlainSearch(@Valid PqmAnomalySearchCriteria criteria, RequestInfo requestInfo) {
		List<PqmAnomaly> pqmAnomalyList = getPqmAnomalyPlainSearch(criteria);	
		return pqmAnomalyList;
	}
	
	public List<PqmAnomaly> anomalySearch(@Valid PqmAnomalySearchCriteria criteria) {
        return anomalyRepository.getAnomalyDataForCriteria(criteria);
	}
	
	private List<PqmAnomaly> getPqmAnomalyPlainSearch(@Valid PqmAnomalySearchCriteria criteria) {
		if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit())
			criteria.setLimit(config.getMaxSearchLimit());

		List<String> ids = null;

		if (criteria.getIds() != null && !criteria.getIds().isEmpty())
			ids = criteria.getIds();
		else
			ids = anomalyRepository.fetchPqmAnomalyIds(criteria);

		if (ids.isEmpty())
			return Collections.emptyList();

		return anomalyRepository.getPqmAnomalyPlainSearch(PqmAnomalySearchCriteria.builder().ids(ids).build());
	}
}
