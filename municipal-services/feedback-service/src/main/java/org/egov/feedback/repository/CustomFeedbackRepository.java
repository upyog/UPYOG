package org.egov.feedback.repository;

import org.egov.feedback.entity.FeedbackSubmissionEntity;
import org.egov.feedback.model.FeedbackSearchCriteria;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;


@Repository
public class CustomFeedbackRepository{
	
    @Autowired
    private EntityManager entityManager;

    public List<FeedbackSubmissionEntity> findFeedbacksByCriteria(FeedbackSearchCriteria criteria) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<FeedbackSubmissionEntity> criteriaQuery = criteriaBuilder.createQuery(FeedbackSubmissionEntity.class);
        Root<FeedbackSubmissionEntity> root = criteriaQuery.from(FeedbackSubmissionEntity.class);

        Predicate predicate = criteriaBuilder.conjunction();  // Start with a 'true' condition

        // Add conditions based on the input parameters
        if (criteria.getStatus() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"),criteria.getStatus()));
//            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("status"), "%" + criteria.getStatus() + "%"));

        }
        if (criteria.getEmail() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("email"), criteria.getEmail()));
        }
        if (criteria.getAction() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("action"), criteria.getAction()));
        }
        
        if (criteria.getApplicationNo() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("applicationNo"), criteria.getApplicationNo()));
        }
        
        if (criteria.getMobileNo() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("mobileNo"),criteria.getMobileNo()));
        }
        
        if (criteria.getServiceId() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("serviceId"),criteria.getServiceId()));
        }
        
        if (criteria.getFeedbackType() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("feedbackType"),criteria.getFeedbackType()));
        }
        
        if (criteria.getRating() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("rating"),criteria.getRating()));
        }
        
        if (criteria.getFrom() != null && criteria.getTo() != null) {
            predicate = criteriaBuilder.and(predicate, 
                criteriaBuilder.between(root.get("createdTime"), criteria.getFrom(), criteria.getTo())
            );
        } else if (criteria.getFrom() != null) {
            predicate = criteriaBuilder.and(predicate, 
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdTime"), criteria.getFrom())
            );
        } else if (criteria.getTo() != null) {
            predicate = criteriaBuilder.and(predicate, 
                criteriaBuilder.lessThanOrEqualTo(root.get("createdTime"), criteria.getTo())
            );
        }

        criteriaQuery.where(predicate);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}



