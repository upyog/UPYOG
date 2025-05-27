package org.egov.fsm.web.model.worker.repository;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.egov.fsm.config.FSMConfiguration;
import org.egov.fsm.fsmProducer.FSMProducer;
import org.egov.fsm.web.model.worker.Worker;
import org.egov.fsm.web.model.worker.WorkerRequest;
import org.egov.fsm.web.model.worker.WorkerSearchCriteria;
import org.egov.fsm.web.model.worker.repository.querybuilder.FsmWorkerQueryBuilder;
import org.egov.fsm.web.model.worker.repository.rowmapper.FsmWorkerRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FsmWorkerRepository {

  @Autowired
  private FSMProducer producer;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private FsmWorkerRowMapper fsmWorkerRowMapper;

  @Autowired
  private FsmWorkerQueryBuilder fsmWorkerQueryBuilder;

  @Autowired
  private FSMConfiguration config;

  public List<Worker> create(List<Worker> workers) {
    producer.push(config.getCreateFsmWorkerTopic(), WorkerRequest.builder()
        .workers(workers)
        .build());
    return workers;
  }

  public List<Worker> update(List<Worker> workers) {
    producer.push(config.getUpdateFsmWorkerTopic(), WorkerRequest.builder()
        .workers(workers)
        .build());
    return workers;
  }

  public List<Worker> getWorkersData(WorkerSearchCriteria workerSearchCriteria) {
    List<Object> preparedStmtList = new ArrayList<>();
    String query = fsmWorkerQueryBuilder.getWorkerSearchQuery(workerSearchCriteria,
        preparedStmtList);
    log.info("Workers Search Query" + query);
    return jdbcTemplate.query(query, preparedStmtList.toArray(), fsmWorkerRowMapper);
  }

}
