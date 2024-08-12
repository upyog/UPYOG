package digit.validators;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import digit.bmc.model.SchemeCriteria;
import digit.bmc.model.UserCompleteDetails;
import digit.bmc.model.VerificationDetails;
import digit.common.CriteriaType;
import digit.repository.SchemeApplicationRepository;
import digit.repository.SchemeBeneficiarySearchCritaria;
import digit.repository.UserRepository;
import digit.repository.UserSearchCriteria;
import digit.web.models.EligibilityResponse;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;
import digit.web.models.SchemeApplicationSearchCriteria;
import digit.web.models.SchemeBeneficiaryDetails;
import digit.web.models.SchemeValidationResponse;
import digit.web.models.user.DocumentDetails;
import digit.web.models.user.QualificationDetails;
import digit.web.models.user.UserDetails;



@Service
public class SchemeApplicationValidator {

    private final SchemeApplicationRepository repository;

    private final SchemeBeneficiarySearchCritaria schemeBeneficiarySearchCritaria;
    
    @Autowired
    UserRepository userRepository;

    EligibilityResponse eligibilityResponse = new EligibilityResponse();

    /**
     * Constructor for SchemeApplicationValidator with repository
     * injection.machineTaken
     *
     * @param repository The repository to be injected.
     */

    @Autowired(required = true)
    public SchemeApplicationValidator(SchemeApplicationRepository repository,
            SchemeBeneficiarySearchCritaria schemeBeneficiarySearchCritaria) {
        this.repository = repository;
        this.schemeBeneficiarySearchCritaria = schemeBeneficiarySearchCritaria;
    }

    /**
     * Validates the SchemeApplicationRequest.
     * Checks if the tenantId is present in each SchemeApplication.
     *
     * @param schemeApplicationRequest The request to be validated.
     */
    public void validateSchemeApplication(SchemeApplicationRequest schemeApplicationRequest) {
        schemeApplicationRequest.getSchemeApplications().forEach(application -> {
            if (ObjectUtils.isEmpty(application.getTenantId())) {
                throw new CustomException("BMC_APP_ERR", "tenantId is mandatory for creating scheme applications");
            }
        });
    }

    /**
     * Validates the existence of a SchemeApplication.
     *
     * @param schemeApplication The application to be validated for existence.
     * @return The existing SchemeApplication if found.
     */
    public SchemeApplication validateApplicationExistence(SchemeApplication schemeApplication) {
        List<SchemeApplication> existingApplications = repository.getApplications(
                SchemeApplicationSearchCriteria.builder()
                        .applicationNumber(schemeApplication.getApplicationNumber())
                        .build());

        if (existingApplications.isEmpty()) {
            throw new CustomException("BMC_APP_NOT_FOUND",
                    "No scheme application found with the given application number");
        }

        return existingApplications.get(0);
    }

    public SchemeValidationResponse criteriaCheck(SchemeApplicationRequest request) {
        SchemeValidationResponse response = new SchemeValidationResponse();
        
        UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
        userSearchCriteria.setOption("full");
        userSearchCriteria.setTenantId(request.getRequestInfo().getUserInfo().getTenantId());
        userSearchCriteria.setUserId(request.getRequestInfo().getUserInfo().getId());
        List<UserDetails> userDetails = userRepository.getUserDetails(userSearchCriteria);
        request.setAadhardob(userDetails.get(0).getAadharUser().getAadharDob());
        request.setGender(userDetails.get(0).getAadharUser().getGender());
        request.setDivyangPercent(userDetails.get(0).getDivyang().getDivyangpercent());
        List<String> documentNames = userDetails.get(0).getDocumentDetails().stream()
                .map(DocumentDetails::getDocumentName)
                .collect(Collectors.toList());
        Long maxQualificationId = userDetails.get(0).getQualificationDetails().stream()
                .map(QualificationDetails::getQualificationId)
                .max(Comparator.naturalOrder())
                .orElse(null);

        boolean age = true, disability = true, gender = false, income = true, education = true, document = true;
        Long schemeId = request.getSchemeId().longValue();
        List<SchemeCriteria> criteriaList = repository.getCriteriaBySchemeIdAndType(schemeId);

        for (SchemeCriteria criteria : criteriaList) {
            CriteriaType criteriaType = CriteriaType.fromDisplayName(criteria.getCriteriaType());
            String condition = criteria.getCriteriaCondition();
            String value = criteria.getCriteriaValue();

            switch (criteriaType) {
                case GENDER:
                    if (request.getGender() != null && !gender) {
                        gender = evaluateCondition(request.getGender().toLowerCase(), condition, value.toLowerCase());
                    }
                    response.setGenderEligibility(gender);
                    break;

                case DISABILITY:
                    if (disability) {
                        disability = evaluateCondition(request.getDivyangPercent(), condition,
                                Double.parseDouble(value));
                    }
                    response.setDisability(disability);
                    break;

                case AGE:
                    if (age) {
                        int dobAge = calculateAge(request.getAadhardob());
                        age = evaluateCondition(dobAge, condition, Integer.parseInt(value));
                    }
                    response.setAgeEligibility(age);
                    break;

                case INCOME:
                    if (income) {
                        income = evaluateCondition(request.getIncome(), condition, Double.parseDouble(value));
                    }
                    response.setIncomeEligibility(income);
                    break;

                case EDUCATION:
                    if (education) {
                        education = evaluateCondition(maxQualificationId, condition,
                                Boolean.parseBoolean(value));
                    }
                    response.setEducationEligibility(education);
                    break;
                case DOCUMENT :
                    if(document) {
                        document = false;
                        for (String documentName : documentNames ) {
                            document = evaluateCondition(documentName,condition,value); 
                            if(document)
                               break;
                        }
                        
                    }
                    response.setRationCardEligibility(document);
                    break;
  
                default:
                    throw new IllegalArgumentException("Invalid criteria: " + criteriaType);
            }

        }
        response.setSchemeType(repository.getSchemeById(schemeId));
        StringBuilder message = new StringBuilder();
        if(!age){
            message.append("Age is not eligible for this scheme ");
            message.append("\n");
        }
        if(!disability){
            message.append("Disability is not eligible for this scheme ");
            message.append("\n");
        }
        if(!income){
            message.append("income is not eligible for this scheme ");
            message.append("\n");
        }
        if(!education){
            message.append("Education is not eligible for this scheme ");
            message.append("\n");
        }
        if(!document){
            message.append("Document is not eligible for this scheme ");
            message.append("\n");
        }
        response.setError(message);
        return response;
    }

    public boolean evaluateCondition(Object obj1, String condition, Object obj2) {

        Comparable c1 = (Comparable) obj1;
        Comparable c2 = (Comparable) obj2;
        if (c1 != null) {
            switch (condition) {
                case "=":
                    return c1.compareTo(c2) == 0;
                case ">=":
                    return c1.compareTo(c2) >= 0;
                case "<=":
                    return c1.compareTo(c2) <= 0;
                case ">":
                    return c1.compareTo(c2) > 0;
                case "<":
                    return c1.compareTo(c2) < 0;
                case "!=":
                    return c1.compareTo(c2) != 0;
                default:
                    throw new IllegalArgumentException("Invalid condition: " + condition);
            }
        } else
            return false;
    }

    public static int calculateAge(Date dob) {
        try {
            LocalDate birthDate = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate currentDate = LocalDate.now();
            return Period.between(birthDate, currentDate).getYears();
        } catch (Exception e) {
            return -1;
        }
    }

    public EligibilityResponse getBeneficiaryInfo(UserCompleteDetails user) {
        schemeBeneficiarySearchCritaria.setUserId(user.getRequestInfo().getUserInfo().getId());
        schemeBeneficiarySearchCritaria.setTenantId(user.getRequestInfo().getUserInfo().getTenantId());
        schemeBeneficiarySearchCritaria.setSubmitted(true);
        schemeBeneficiarySearchCritaria.setForMachine(true);
        schemeBeneficiarySearchCritaria.setForCourse(true);
        schemeBeneficiarySearchCritaria.setForPension(true);
        schemeBeneficiarySearchCritaria.setName("pension");

        List<SchemeBeneficiaryDetails> schemeBeneficiaryDetails = repository
                .initialEligibilityCheck(schemeBeneficiarySearchCritaria);

        eligibilityResponse.setMachineTaken(hasTakenMachineInLastFiveYears(schemeBeneficiaryDetails));
        eligibilityResponse.setCourseTaken(hasTakenCourse(schemeBeneficiaryDetails));

        eligibilityResponse.setPensionApplied(hasAppliedPension(schemeBeneficiaryDetails));

        return eligibilityResponse;

    }

    public boolean hasTakenMachineInLastFiveYears(List<SchemeBeneficiaryDetails> schemeBeneficiaryDetails) {

        if (schemeBeneficiaryDetails == null || schemeBeneficiaryDetails.isEmpty()) {
            return false;
        }
        Instant lastBenefitInstant = schemeBeneficiaryDetails.get(0).getStartDate();
        if (lastBenefitInstant == null) {
            return false;
        }
        Date lastBenefitDate = Date.from(lastBenefitInstant);
        Date currentDate = new Date();
        long diffInMillis = currentDate.getTime() - lastBenefitDate.getTime();
        long diffInYears = diffInMillis / (1000L * 60 * 60 * 24 * 365);
        return diffInYears < 5;
    }

    public boolean hasTakenCourse(List<SchemeBeneficiaryDetails> schemeBeneficiaryDetails) {

        if (schemeBeneficiaryDetails == null || schemeBeneficiaryDetails.isEmpty()) {
            return false;
        }
        Instant startDate = schemeBeneficiaryDetails.get(0).getStartDate();
        Instant endDate = schemeBeneficiaryDetails.get(0).getEndDate();
        if (startDate == null) {
            return false;
        }
        Date lastBenefitDate = Date.from(endDate);
        Date currentDate = new Date();
        long diffInMillis = currentDate.getTime() - lastBenefitDate.getTime();
        long diffInYears = diffInMillis / (1000L * 60 * 60 * 24 * 365);
        return diffInYears <= 1;
    }
    // sundeep: need to check later
    public EligibilityResponse isAddressFromBMCArea(UserCompleteDetails user) {
        eligibilityResponse.setAddressValidated("Mumbai".equalsIgnoreCase(user.getAddress().getCity()));
        return eligibilityResponse;
    }

    public boolean hasAppliedPension(List<SchemeBeneficiaryDetails> schemeBeneficiaryDetails) {

        if (schemeBeneficiaryDetails == null || schemeBeneficiaryDetails.isEmpty()) {
            return false;
        }
        if (schemeBeneficiaryDetails.get(0).getHas_applied_for_pension() > 0) {
            return true;
        }

        return false;
    }

    private boolean hasValidUDID(SchemeApplicationRequest request) {
        return request.getUdid() != null;// && request.getDivyangPercent() >= requiredDisabilityPercentage;
    }

    private boolean hasMinimumEducation(SchemeApplicationRequest request) {
        String education = request.getEducationLevel();
        List<String> qualification = repository.getQualifications();

        return qualification.contains(education);
    }

}
