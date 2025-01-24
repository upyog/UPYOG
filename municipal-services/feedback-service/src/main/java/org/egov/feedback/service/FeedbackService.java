package org.egov.feedback.service;

import org.egov.tracer.model.CustomException;
import org.egov.feedback.producer.Producer;

import java.util.List;

import org.egov.feedback.entity.FeedbackSubmissionEntity;
import org.egov.feedback.model.FeedbackCreateRequest;
import org.egov.feedback.model.FeedbackSubmission;  // Corrected the typo from FeedbackSubmision
import org.egov.feedback.model.FeedbackSearchRequest;
import org.egov.feedback.model.FeedbackSearchCriteria;
import org.egov.feedback.repository.FeedbackSubmissionRepository;
import org.egov.feedback.repository.CustomFeedbackRepository;
import org.egov.feedback.model.AuditDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FeedbackService {
    
    @Autowired
    private FeedbackSubmissionRepository repository;
    @Autowired
    private CustomFeedbackRepository customRepository;
    
    @Autowired
    private Producer producer;

    // Method to handle creating and saving feedback
    public void createFeedback(FeedbackCreateRequest request) {
        validateFeedback(request); // Validate the incoming feedback
        enrichFeedbackRequest(request,true); // Enrich with additional information (if needed)
        
//        FeedbackSubmissionEntity e = new FeedbackSubmissionEntity();

        // Assuming you want to save each submission from the request
        if (!CollectionUtils.isEmpty(request.getFeedbackSubmisions())) {
            for (FeedbackSubmission feedbackSubmission : request.getFeedbackSubmisions()) {
            	
                saveFeedback(feedbackSubmission); // Save the feedback
            }
        }
    }

    // Method to save individual feedback submission
    public void saveFeedback(FeedbackSubmission e) {
//    	create-feedback-submission
		producer.push("create-feedback-submission", e);

//        return repository.save(e); // Saves feedback to the database
    }

    public String getApplicationNo(Long seq) {
        String template = "FEEDSUB-000000[SEQ]";
        String number = template.replace("[SEQ]", String.valueOf(seq));
        return number;
    }
    // Method to validate the feedback
    public void validateFeedback(FeedbackCreateRequest request) {
        if (CollectionUtils.isEmpty(request.getFeedbackSubmisions())) {
            throw new CustomException("FEEDBACK_SUBMISSION_EMPTY", "Provide feedback submissions.");
        }
        else {
        	for ( FeedbackSubmission feedback: request.getFeedbackSubmisions()) {
        		if(feedback.getMobileNo() == null || feedback.getMobileNo().isEmpty()) {
                    throw new CustomException("FEEDBACK_MOBILE_NO_EMPTY", "mobile number cannot be empty.");
        		}
        	}
        	
        }
    }

    // Placeholder method to enrich the feedback request (can be implemented with your custom logic)
    public void enrichFeedbackRequest(FeedbackCreateRequest request,boolean isCreate) {

    	for ( FeedbackSubmission feedback: request.getFeedbackSubmisions()) {
    		Long seq = repository.getNextSequenceValue();
    		feedback.setId(seq);
    		feedback.setApplicationNo(getApplicationNo(seq));
    		feedback.setUserUuid(request.getRequestInfo().getUserInfo().getUuid());
            AuditDetails auditDetails = getAuditDetails(request.getRequestInfo().getUserInfo().getId().toString(), isCreate);
    		feedback.setAuditDetails(auditDetails);
    		if(isCreate) {
        		feedback.setStatus("SUBMITED");
        		feedback.setAction("INITIATED");
    		}
//    		else
//    		{
//        		feedback.setStatus(null);
//    		}
    		
    	}
    }
    
    public AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if(isCreate)
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time).build();
        else
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
    }
    
    
    public List <FeedbackSubmissionEntity> searchFeedback(FeedbackSearchRequest feedbackSearchReq) {
//		log.info("here");
    	return customRepository.findFeedbacksByCriteria(feedbackSearchReq.getFeedbackSearchCriteria());
    }
}
