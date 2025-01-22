package org.egov.feedback.repository;

import org.egov.feedback.entity.FeedbackSubmissionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface  FeedbackSubmissionRepository extends JpaRepository<FeedbackSubmissionEntity, Long>{

	 // You can define custom query methods here if needed

    // Example: Find submissions by serviceId
    List<FeedbackSubmissionEntity> findByServiceId(String serviceId);

    // Example: Find submissions by applicationNo
    List<FeedbackSubmissionEntity> findByApplicationNo(String applicationNo);

    // Example: Find all submissions with a specific rating
    List<FeedbackSubmissionEntity> findByRating(String rating);
    
    @Query(value = "SELECT nextval('seq_eg_feedback_submission_no')", nativeQuery = true)
    Long getNextSequenceValue();
    
    @Query(value = "SELECT * FROM egov_feedback_submission f WHERE " +
    	       "(:status IS NULL OR f.status = :status)", nativeQuery = true)
    	List<FeedbackSubmissionEntity> findFeedbacks(@Param("status") String status);
   
}