package org.egov.pqm.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
@Configuration
@PropertySource("classpath:application.properties")
public class ServiceConfiguration {

  @Value("${egov.test.default.limit}")
  private Integer defaultLimit;

  @Value("${egov.test.default.offset}")
  private Integer defaultOffset;

  @Value("${egov.test.max.limit}")
  private Integer maxSearchLimit;

  // MDMS
  @Value("${egov.mdms.host}")
  private String mdmsHost;

  @Value("${egov.mdms.search.endpoint}")
  private String mdmsEndPoint;

  @Value("${egov.mdms.v2.host}")
  private String mdmsHostv2;

  @Value("${egov.mdms.search.v2.endpoint}")
  private String mdmsv2EndPoint;

  @Value("${egov.mdms.search.v2.max.limit}")
  private Integer mdmsv2MaxLimit;



  // Kafka Topic
  @Value("${egov.test.create.kafka.topic}")
  private String testSaveTopic;

  @Value("${egov.test.create.event.kafka.topic}")
  private String testSaveEventTopic;

  @Value("${egov.test.update.kafka.topic}")
  private String testUpdateTopic;

  @Value("${egov.test.update.event.kafka.topic}")
  private String testUpdateEventTopic;

  @Value("${egov.test.update.workflow.kafka.topic}")
  private String testWorkflowTopic;

  @Value("${egov.pqm.anomaly.create.kafka.topic}")
  private String anomalyCreateTopic;

  @Value("${egov.pqm.anomaly.testResultNotSubmitted.kafka.topic}")
  private String testResultNotSubmittedKafkaTopic;

  @Value("${egov.plant.user.create.kafka.topic}")
  private String plantUserSaveTopic;

  @Value("${egov.plant.user.update.kafka.topic}")
  private String plantUserUpdateTopic;

  @Value("${egov.test.document.update.kafka.topic}")
  private String updateTestDocumentsTopic;

  //workflow
  @Value("${create.pqm.workflow.name}")
  private String businessServiceValue;

  @Value("${workflow.context.path}")
  private String wfHost;

  @Value("${workflow.transition.path}")
  private String wfTransitionPath;

  @Value("${workflow.businessservice.search.path}")
  private String wfBusinessServiceSearchPath;

  @Value("${workflow.process.path}")
  private String wfProcessPath;


  // Idgen Config
  @Value("${egov.idgen.host}")
  private String idGenHost;

  @Value("${egov.idgen.path}")
  private String idGenPath;

  @Value("${egov.idgen.pqm.id.name}")
  private String idName;

  @Value("${egov.idgen.pqm.id.format}")
  private String idFormat;

  //Individual servcie
  @Value("${egov.individual.host}")
  private String individualHost;

  @Value("${egov.individual.search.endpoint}")
  private String individualSearchEndpoint;
  
  //User Config
  @Value("${egov.user.host}")
  private String userHost;

  @Value("${egov.user.search.path}")
  private String userSearchEndpoint;

  // PQM Anomaly Service Config
  @Value("${egov.pqm.anomaly.host}")
  private String pqmAnomalyHost;

  @Value("${egov.pqm.anomaly.search.endpoint}")
  private String pqmAnomalySearchEndpoint;


  //tenantId
  @Value("${egov.statelevel.tenantid}")
  private String egovStateLevelTenantId;

  // Localization
  @Value("${egov.localization.host}")
  private String localizationHost;

  @Value("${egov.localization.context.path}")
  private String localizationContextPath;

  @Value("${egov.localization.search.endpoint}")
  private String localizationSearchEndpoint;

  @Value("${egov.localization.statelevel}")
  private Boolean isLocalizationStateLevel;


  //PDF
  @Value("${egov.pdfservice.link}")
  private String pdfServiceLink;

  @Value("${egov.pdfservice.host}")
  private String pdfServiceHost;



}
