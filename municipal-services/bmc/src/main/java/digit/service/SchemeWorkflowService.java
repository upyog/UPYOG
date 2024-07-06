package digit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.repository.SchemeWorkflowRepository;
import digit.web.models.SchemeWorkflow;

@Service
public class SchemeWorkflowService {

     private final SchemeWorkflowRepository schemeWorkflowRepository;

    @Autowired
    public SchemeWorkflowService(SchemeWorkflowRepository schemeWorkflowRepository) {
        this.schemeWorkflowRepository = schemeWorkflowRepository;
    }

    public List<SchemeWorkflow> getAllActions() {
        return schemeWorkflowRepository.findAll();
    } 

}
