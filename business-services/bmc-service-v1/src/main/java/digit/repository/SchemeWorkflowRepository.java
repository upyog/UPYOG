package digit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import digit.web.models.SchemeWorkflow;

@Repository
public interface SchemeWorkflowRepository extends  JpaRepository<SchemeWorkflow, String>{

}
