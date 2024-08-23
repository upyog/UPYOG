package digit.validators;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
        StringBuilder message = new StringBuilder();
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
        boolean age = true, disability = true, gender = false, income = true, education = true, document = false,
                benefitted = false,dflag = false,gflag=false;
        Long schemeId = request.getSchemeId().longValue();
        List<SchemeCriteria> criteriaList = repository.getCriteriaBySchemeIdAndType(schemeId);

        response.setUserOtherDetails(userDetails.get(0).getUserOtherDetails());

        Optional<SchemeCriteria> matchingCriteria = criteriaList.stream()
                    .filter(benifittedCriteria -> "benifitted".equalsIgnoreCase(benifittedCriteria.getCriteriaType()))
                    .findFirst();
            if (matchingCriteria.isPresent()) {
                SchemeCriteria benifittedCriteria = matchingCriteria.get();

                schemeBeneficiarySearchCritaria.setUserId(request.getRequestInfo().getUserInfo().getId());
                schemeBeneficiarySearchCritaria.setTenantId(request.getRequestInfo().getUserInfo().getTenantId());
                schemeBeneficiarySearchCritaria.setOptedId(schemeId);
                // schemeBeneficiarySearchCritaria.setSubmitted(true);
                List<SchemeBeneficiaryDetails> schemeBeneficiaryDetailsList = repository
                        .initialEligibilityCheck(schemeBeneficiarySearchCritaria);
                for (SchemeBeneficiaryDetails schemeBeneficiaryDetails : schemeBeneficiaryDetailsList) {
                    if (schemeBeneficiaryDetails.getMachineId() != 0) {

                        benefitted = evaluateCondition(hasTakenMachineInLastFiveYears(schemeBeneficiaryDetails),
                                benifittedCriteria.getCriteriaCondition(),
                                Long.parseLong(benifittedCriteria.getCriteriaValue().replace(",", "")));

                    } else if (schemeBeneficiaryDetails.getCourseId() != 0 ) {

                        benefitted = evaluateCondition(hasTakenCourse(schemeBeneficiaryDetails),
                                benifittedCriteria.getCriteriaCondition(),
                                Long.parseLong(benifittedCriteria.getCriteriaValue().replace(",", "")));
                    } else {
                        
                        benefitted = evaluateCondition(hasAppliedPension(schemeBeneficiaryDetails),
                                benifittedCriteria.getCriteriaCondition(),
                                Long.parseLong(benifittedCriteria.getCriteriaValue().replace(",", "")));;
                    }
                    if(benefitted){
                        response.setBenifittedDate(Date.from(schemeBeneficiaryDetails.getStartDate()));
                        response.setSchemeName(schemeBeneficiaryDetails.getSchemeGroupName());
                    }
                }

            }
            if(benefitted){

                response.setError(message.append("you have already benifitted for ")
                .append(response.getSchemeName())
                .append(response.getBenifittedDate().toString()));
                return response;
            }
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
                    gflag =true;
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
                                Long.parseLong(value));
                    }
                    response.setEducationEligibility(education);
                    break;
                case DOCUMENT:
                    if (!document) {
                        for (String documentName : documentNames) {
                            document = evaluateCondition(documentName, condition, value);
                            if (document) {
                                break;
                            }
                        }
                        response.setRationCardEligibility(document);
                        dflag =true;
                    }

                    break;
                case BENIFITTED:
                    break;

                default:
                    throw new IllegalArgumentException("Invalid criteria: " + criteriaType);
            }

        }
        response.setSchemeType(repository.getSchemeById(schemeId));
        if (!age) {
            message.append("Age is not eligible for this scheme ");
            message.append("\n");
        }
        if (!disability) {
            message.append("Disability is not eligible for this scheme ");
            message.append("\n");
        }
        if (!income) {
            message.append("income is not eligible for this scheme ");
            message.append("\n");
        }
        if (!education) {
            message.append("Education is not eligible for this scheme ");
            message.append("\n");
        }
        if (!document && dflag) {
            message.append("Document is not eligible for this scheme ");
            message.append("\n");
        }
        if (!gender && gflag) {
            message.append("Gender is not eligible for this scheme ");
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
            Calendar cal = Calendar.getInstance();
            cal.setTime(dob);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1; 
            int day = cal.get(Calendar.DAY_OF_MONTH);
            LocalDate birthDate = LocalDate.of(year, month, day);
            LocalDate currentDate = LocalDate.now();
            return Period.between(birthDate, currentDate).getYears();
        } catch (Exception e) {
            return -1;
        }
    }

    public long hasTakenMachineInLastFiveYears(SchemeBeneficiaryDetails schemeBeneficiaryDetails) {

        if (schemeBeneficiaryDetails == null) {
            return -1;
        }
        Instant lastBenefitInstant = schemeBeneficiaryDetails.getStartDate();
        if (lastBenefitInstant == null) {
            return -1;
        }
        Date lastBenefitDate = Date.from(lastBenefitInstant);
        Date currentDate = new Date();
        long diffInMillis = currentDate.getTime() - lastBenefitDate.getTime();
        long diffInYears = diffInMillis / (1000L * 60 * 60 * 24 * 365);
        return diffInMillis;
    }

    public long hasTakenCourse(SchemeBeneficiaryDetails schemeBeneficiaryDetails) {

        if (schemeBeneficiaryDetails == null) {
            return -1;
        }
        Instant lastBenefitInstant = schemeBeneficiaryDetails.getStartDate();
        if (lastBenefitInstant == null) {
            return -1;
        }
        Date lastBenefitDate = Date.from(lastBenefitInstant);
        Date currentDate = new Date();
        long diffInMillis = currentDate.getTime() - lastBenefitDate.getTime();
        long diffInYears = diffInMillis / (1000L * 60 * 60 * 24 * 365);
        return diffInMillis;
    }

    // sundeep: need to check later
    public EligibilityResponse isAddressFromBMCArea(UserCompleteDetails user) {
        eligibilityResponse.setAddressValidated("Mumbai".equalsIgnoreCase(user.getAddress().getCity()));
        return eligibilityResponse;
    }

    public long hasAppliedPension(SchemeBeneficiaryDetails schemeBeneficiaryDetails) {
        
        if (schemeBeneficiaryDetails == null) {
            return -1;
        }
        Instant lastBenefitInstant = schemeBeneficiaryDetails.getStartDate();
        if (lastBenefitInstant == null) {
            return -1;
        }
        Date lastBenefitDate = Date.from(lastBenefitInstant);
        Date currentDate = new Date();
        long diffInMillis = currentDate.getTime() - lastBenefitDate.getTime();
        long diffInYears = diffInMillis / (1000L * 60 * 60 * 24 * 365);
        return diffInMillis;
    }

}
