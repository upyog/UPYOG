package org.egov.pqm.repository;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.pqmProducer.PqmProducer;
import org.egov.pqm.repository.querybuilder.TestQueryBuilder;
import org.egov.pqm.repository.rowmapper.DocumentRowMapper;
import org.egov.pqm.repository.rowmapper.QualityCriteriaRowMapper;
import org.egov.pqm.repository.rowmapper.TestRowMapper;
import org.egov.pqm.web.model.Document;
import org.egov.pqm.web.model.DocumentResponse;
import org.egov.pqm.web.model.QualityCriteria;
import org.egov.pqm.web.model.Test;
import org.egov.pqm.web.model.TestRequest;
import org.egov.pqm.web.model.TestResponse;
import org.egov.pqm.web.model.TestSearchCriteria;
import org.egov.pqm.web.model.TestSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class TestRepository {

  @Autowired
  private TestQueryBuilder pqmQueryBuilder;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private TestRowMapper pqmRowMapper;

  @Autowired
  private DocumentRowMapper documentRowMapper;

  @Autowired
  private QualityCriteriaRowMapper qualityCriteriaRowMapper;

  @Autowired
  private PqmProducer producer;

  @Autowired
  private ServiceConfiguration config;

  public void save(TestRequest testRequest) {
    producer.push(config.getTestSaveTopic(), testRequest);
    producer.push(config.getTestSaveEventTopic(), testRequest);
  }

  public void saveAnomaly(String topic, TestRequest testRequest) {
    producer.push(topic, testRequest);
  }


  public void update(TestRequest testRequest) {
    producer.push(config.getTestUpdateTopic(), testRequest);
    producer.push(config.getTestUpdateEventTopic(), testRequest);
  }

  public void updateTestDocuments(TestRequest testRequest){
    producer.push(config.getUpdateTestDocumentsTopic(), testRequest);
  }

  public TestResponse getPqmData(TestSearchRequest testSearchCriteria) {
    List<Object> preparedStmtList = new ArrayList<>();
    String query = pqmQueryBuilder.getPqmSearchQuery(testSearchCriteria, preparedStmtList);
    List<Test> tests = jdbcTemplate.query(query, preparedStmtList.toArray(), pqmRowMapper);
    return TestResponse.builder().tests(tests).totalCount(pqmRowMapper.getFullCount()).build();
  }

  public DocumentResponse getDocumentData(List<String> idList) {
    List<Object> preparedStmtList = new ArrayList<>();
    String query = pqmQueryBuilder.getDocumentSearchQuery(idList, preparedStmtList);
    List<Document> documents = jdbcTemplate.query(query, preparedStmtList.toArray(),
        documentRowMapper);
    return DocumentResponse.builder().documents(documents).build();
  }

  public List<QualityCriteria> getQualityCriteriaData(List<String> idList) {
    List<Object> preparedStmtList = new ArrayList<>();
    String query = pqmQueryBuilder.getQualityCriteriaQuery(idList, preparedStmtList);
    List<QualityCriteria> qualityCriterias = jdbcTemplate.query(query, preparedStmtList.toArray(),
        qualityCriteriaRowMapper);
    return qualityCriterias;
  }

	public List<String> fetchTestIds(TestSearchCriteria testSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		preparedStmtList.add(testSearchCriteria.getOffset());
		preparedStmtList.add(testSearchCriteria.getLimit());
		return jdbcTemplate.query("SELECT id from eg_pqm_tests ORDER BY createdtime offset " + " ? " + "limit ? ",
				preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
	}

	
}
