package org.egov.pt.consumer;


import java.util.HashMap;

import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.Notice;
import org.egov.pt.models.Property;
import org.egov.pt.models.enums.NoticeType;
import org.egov.pt.service.AssessmentNotificationService;
import org.egov.pt.service.NotificationService;
import org.egov.pt.util.PTConstants;
import org.egov.pt.web.contracts.AssessmentRequest;
import org.egov.pt.web.contracts.NoticeRequest;
import org.egov.pt.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationConsumer {

	@Autowired
    private ObjectMapper mapper;
	
	@Autowired
	private PropertyConfiguration configs;

	@Autowired
    private AssessmentNotificationService assessmentNotificationService;
	
	@Autowired
	private NotificationService notifService;
    @KafkaListener(topics = {"${egov.pt.assessment.create.topic}",
    						 "${egov.pt.assessment.update.topic}",
    						 "${persister.update.property.topic}",
    						 "${persister.save.property.topic}",
    						 "${persister.update.property.bifurcation.inactive}",
                             "${persister.save.notice.topic}"})
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		try {

			if (topic.equalsIgnoreCase(configs.getCreateAssessmentTopic()) || topic.equalsIgnoreCase(configs.getUpdateAssessmentTopic())) {

				AssessmentRequest request = mapper.convertValue(record, AssessmentRequest.class);
				assessmentNotificationService.process(topic, request);
			} else if (topic.equalsIgnoreCase(configs.getSavePropertyTopic()) || topic.equalsIgnoreCase(configs.getUpdatePropertyTopic())) {

				PropertyRequest request = mapper.convertValue(record, PropertyRequest.class);

				if (!request.getProperty().isOldDataEncryptionRequest()) {
					if (PTConstants.MUTATION_PROCESS_CONSTANT.equalsIgnoreCase(request.getProperty().getCreationReason().toString())) {

						notifService.sendNotificationForMutationNew(request);
					} else if(PTConstants.CREATE_PROCESS_CONSTANT.equalsIgnoreCase(request.getProperty().getCreationReason().toString())||
							PTConstants.AMALGAMATION_PROCESS_CONSTANT.equalsIgnoreCase(request.getProperty().getCreationReason().toString())||
							PTConstants.BIFURCATION_PROCESS_CONSTANT.equalsIgnoreCase(request.getProperty().getCreationReason().toString())
							){

						notifService.sendNotificationForCreate(request);
						
					}
					else if(PTConstants.UPDATE_PROCESS_CONSTANT.equalsIgnoreCase(request.getProperty().getCreationReason().toString())){

						notifService.sendNotificationForUpdate(request);
					}
				}	
			}
			else if (topic.equalsIgnoreCase(configs.getUpdatePropertyForDeactivaingForBifurcationTopic())) {

				PropertyRequest request = mapper.convertValue(record, PropertyRequest.class);
				notifService.processForBifurcation(request,configs.getUpdatePropertyForDeactivaingForBifurcationTopic());
			}
			else if(topic.equalsIgnoreCase(configs.getSavenoticetopic()))
			{
				NoticeRequest noticeRequest=mapper.convertValue(record, NoticeRequest.class);
				if(noticeRequest.getNotice().getNoticeType().equals(NoticeType.NOTICE_ENTER_PREMISE))
				notifService.sendNoticeInformationForEntryPremises(noticeRequest);
			}

        } catch (final Exception e) {

            log.error("Error while listening to value: " + record + " on topic: " + topic + ": ", e);
        }
    }



}
