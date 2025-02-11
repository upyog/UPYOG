package org.egov.pqm.anomaly.finder.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.pqm.anomaly.finder.repository.AnomalyRepository;
import org.egov.pqm.anomaly.finder.web.model.PqmAnomaly;
import org.egov.pqm.anomaly.finder.web.model.Test;
import org.egov.pqm.anomaly.finder.web.model.TestRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestAnomalyFinderValidator {

	@Autowired
	public AnomalyRepository anomalyRepository;
	
	public TestRequest validateCreate(TestRequest testRequest) {
		
		List<String> testIdLists = testRequest.getTests().stream().map(Test::getTestId).collect(Collectors.toList());
		List<Test> tests = testRequest.getTests();
		List<PqmAnomaly> pqmAnomalys = anomalyRepository.getAnomalyFinderData(testIdLists);
		List<String> testIds = pqmAnomalys.stream().map(PqmAnomaly::getTestId).collect(Collectors.toList());

		List<Test> filterTests = tests.stream().filter(test -> !testIds.contains(test.getTestId())).collect(Collectors.toList());
		return TestRequest.builder().tests(filterTests).requestInfo(testRequest.getRequestInfo()).build();
	}
}
