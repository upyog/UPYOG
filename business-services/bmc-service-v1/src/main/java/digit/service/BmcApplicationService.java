package digit.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.egov.common.contract.models.Workflow;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import digit.bmc.model.Divyang;
import digit.bmc.model.Schemes;
import digit.bmc.model.UserSchemeApplication;
import digit.config.BmcConfiguration;
import digit.enrichment.SchemeApplicationEnrichment;
import digit.kafka.Producer;
import digit.repository.SchemeApplicationRepository;
import digit.repository.UserRepository;
import digit.repository.UserSchemeCitizenRepository;
import digit.repository.UserSearchCriteria;
import digit.validators.SchemeApplicationValidator;
import digit.web.models.SMSRequest;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;
import digit.web.models.SchemeApplicationSearchCriteria;
import digit.web.models.SchemeValidationResponse;
import digit.web.models.UserSchemeApplicationRequest;
import digit.web.models.user.DocumentDetails;
import digit.web.models.user.InputTest;
import digit.web.models.user.QualificationDetails;
import digit.web.models.user.UserDetails;
import digit.web.models.user.UserRequest;
import digit.web.models.user.UserSubSchemeMapping;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BmcApplicationService {

    private static final Logger log = LoggerFactory.getLogger(BmcApplicationService.class);
    private final UserSchemeCitizenRepository userschemecitizenRepository;

    private final UserSchemeApplicationService userSchemeApplicationService;
    private final SchemeService schemeService;
    @Autowired
    private SchemeApplicationValidator validator;
    @Autowired
    private SchemeApplicationEnrichment enrichmentUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private SchemeApplicationRepository schemeApplicationRepository;
    @Autowired
    private Producer producer;

    @Autowired
    public BmcApplicationService(UserSchemeApplicationService userSchemeApplicationService, SchemeService schemeService,
            UserSchemeCitizenRepository userschemecitizenRepository) {
        this.userSchemeApplicationService = userSchemeApplicationService;
        this.schemeService = schemeService;
        this.userschemecitizenRepository = userschemecitizenRepository;
    }

    public List<SchemeApplication> searchSchemeApplications(RequestInfo requestInfo,
            SchemeApplicationSearchCriteria schemeApplicationSearchCriteria) {
        // Fetch applications from database according to the given search criteria
        List<SchemeApplication> applications = schemeApplicationRepository
                .getApplications(schemeApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty
        // list
        if (CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        // Enrich user details of applicant objects
        applications.forEach(enrichmentUtil::enrichUserDetailsOnSearch);

        // Otherwise return the found applications
        return applications;
    }

    public List<UserSchemeApplication> rendomizeCitizens(SchemeApplicationRequest schemeApplicationRequest) {
        List<UserSchemeApplication> citizens = userSchemeApplicationService
                .getfirstApprovalCitizen(schemeApplicationRequest);
        log.info("Value returned by getFirstApprovalCitizen: {}", citizens);

        Random random = new Random();
        Collections.shuffle(citizens, random);
        log.info("Shuffled citizens: {}", citizens);

        Long numberOfMachines = schemeApplicationRequest.getSchemeApplications().get(0).getNumberOfMachines();
        log.info("Number of machines: {}", numberOfMachines);
        int numberOfCitizens = Math.min(citizens.size(), numberOfMachines.intValue());
        log.info("Number of citizens to select: {}", numberOfCitizens);

        List<UserSchemeApplication> selectedCitizens = new ArrayList<>();
        List<Long> selectedCitizenIds = new ArrayList<>();
        for (int i = 0; i < numberOfCitizens; i++) {
            UserSchemeApplication citizen = citizens.get(i);
            citizen.setRandomSelection(true);
            selectedCitizens.add(citizen);
            selectedCitizenIds.add(citizen.getId());
        }
        log.info("Selected citizens: {}", selectedCitizens);

        userschemecitizenRepository.updateRandomSelection(selectedCitizenIds);

        return selectedCitizens;
    }

    public UserSchemeApplication saveApplicationDetails(UserSchemeApplicationRequest schemeApplicationRequest)
            throws Exception {

        Long userId = schemeApplicationRequest.getRequestInfo().getUserInfo().getId();
        String tenantId = schemeApplicationRequest.getRequestInfo().getUserInfo().getTenantId();
        Long time = System.currentTimeMillis();

        SMSRequest sms = new SMSRequest("7809840269","hi, how are you");
        producer.push("egov.core.notification.sms",sms);

        SchemeApplicationRequest request = new SchemeApplicationRequest();
        request.setRequestInfo(schemeApplicationRequest.getRequestInfo());
        request.setIncome(Double.parseDouble(
                schemeApplicationRequest.getSchemeApplication().getUpdateSchemeData().getIncome().getValue()));
        request.setSchemeId(schemeApplicationRequest.getSchemeApplication().getSchemes().getId());
        SchemeValidationResponse response = validator.criteriaCheck(request);
        InputTest inputTest = new InputTest();
        inputTest.setUserOtherDetails(response.getUserOtherDetails());
        inputTest.getUserOtherDetails().setIncome(request.getIncome());
        inputTest.getUserOtherDetails().setUserId(userId);
        inputTest.getUserOtherDetails().setTenantId(tenantId);
        inputTest.getUserOtherDetails().setOccupation(
                schemeApplicationRequest.getSchemeApplication().getUpdateSchemeData().getOccupation().getValue());
        if (inputTest.getUserOtherDetails().getDivyang() == null) {
            inputTest.getUserOtherDetails().setDivyang(new Divyang());
        }
        producer.push("upsert-userotherdetails", inputTest);

        if (!ObjectUtils.isEmpty(response.getError()) || response.getError() != null) {
            throw new CustomException("Not eligible for this Scheme",
                    response.getError().toString());
        }
        List<SchemeApplication> schemeApplicationList = new ArrayList<>();
        schemeApplicationList.add(schemeApplicationRequest.getSchemeApplication());
        schemeApplicationRequest.setSchemeApplicationList(schemeApplicationList);
        Schemes scheme = schemeApplicationList.get(0).getSchemes();
        if (ObjectUtils.isEmpty(scheme.getId())) {
            throw new Exception("Scheme id must not be null or empty");
        }
        
        enrichmentUtil.enrichSchemeApplication(schemeApplicationRequest);
        UserSchemeApplication userSchemeApplication = new UserSchemeApplication();
        for (SchemeApplication application : schemeApplicationRequest.getSchemeApplicationList()) {

            userSchemeApplication.setApplicationNumber(application.getApplicationNumber());
            userSchemeApplication.setUserId(userId);
            userSchemeApplication.setTenantId(tenantId);
            userSchemeApplication.setOptedId(application.getSchemes().getId());
            userSchemeApplication.setCreatedBy("system");
            userSchemeApplication.setModifiedBy("system");
            userSchemeApplication.setModifiedOn(time);
            userSchemeApplication.setApplicationStatus(true);
            userSchemeApplication.setFinalApproval(false);
            userSchemeApplication.setFirstApprovalStatus(false);
            userSchemeApplication.setRandomSelection(false);
            userSchemeApplication.setSubmitted(false);
            userSchemeApplication.setVerificationStatus(false);
            userSchemeApplication.setAgreeToPay(application.getUpdateSchemeData().isAgreeToPay());
            userSchemeApplication.setStatement(application.getUpdateSchemeData().isStatement());
            schemeApplicationRequest.setUserSchemeApplication(userSchemeApplication);

        }
        Workflow workflow = new Workflow();
        workflow.setAction("APPLY");
        schemeApplicationRequest.getSchemeApplicationList().get(0).setWorkflow(workflow);
        workflowService.updateWorkflowStatus(schemeApplicationRequest);
        producer.push("save-user-scheme-application", schemeApplicationRequest);

        for (DocumentDetails details : schemeApplicationRequest.getSchemeApplication().getUpdateSchemeData()
                .getDocuments()) {
            details.setAvailable(true);
            details.setTenantId(tenantId);
            details.setUserId(userId);
            details.setCreatedBy("system");
            details.setModifiedBy("system");
            details.setModifiedOn(time);
        }
        producer.push("upsert-user-document", schemeApplicationRequest);
        
        UserSubSchemeMapping userSubSchemeMapping = new UserSubSchemeMapping();
        userSubSchemeMapping.setApplicationNumber(userSchemeApplication.getApplicationNumber());
        userSubSchemeMapping.setCreatedBy("System");
        userSubSchemeMapping.setCreatedOn(time);
        userSubSchemeMapping.setUserId(userId);
        userSubSchemeMapping.setTenantId(tenantId);
        if (!ObjectUtils.isEmpty(schemeApplicationRequest.getSchemeApplication().getSchemeType().getId())) {
            String type = schemeApplicationRequest.getSchemeApplication().getSchemeType().getType().toLowerCase();
            Long schemeTypeId = schemeApplicationRequest.getSchemeApplication().getSchemeType().getId();
            switch (type) {
                case "machine":
                    userSubSchemeMapping.setMachineId(schemeTypeId);
                    break;
                case "course":
                    userSubSchemeMapping.setCourseId(schemeTypeId);
                    break;
            }
        }
        schemeApplicationRequest.setUserSubSchemeMapping(userSubSchemeMapping);
        producer.push("upsert-usersubschememapping", schemeApplicationRequest);

        return userSchemeApplication;
    }

}
