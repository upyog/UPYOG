package org.egov.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.egov.feedback.entity.FeedbackSubmissionEntity;


import java.util.List;

public interface CustomFeedbackInterface{  
	List<FeedbackSubmissionEntity> findFeedbacksByCriteria(String name, String email, Integer age);
}