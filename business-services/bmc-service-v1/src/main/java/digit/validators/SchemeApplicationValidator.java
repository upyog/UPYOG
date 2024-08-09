package digit.validators;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import digit.repository.SchemeApplicationRepository;
import digit.repository.SchemeBeneficiarySearchCritaria;
import digit.web.models.EligibilityResponse;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;
import digit.web.models.SchemeApplicationSearchCriteria;
import digit.web.models.SchemeBeneficiaryDetails;

@Service
public class SchemeApplicationValidator {

    private final SchemeApplicationRepository repository;
    
    private  final SchemeBeneficiarySearchCritaria schemeBeneficiarySearchCritaria;
    
     EligibilityResponse eligibilityResponse = new EligibilityResponse();

    /**
     * Constructor for SchemeApplicationValidator with repository injection.machineTaken
     *
     * @param repository The repository to be injected.
     */
 
    @Autowired(required=true)
    public SchemeApplicationValidator(SchemeApplicationRepository repository, SchemeBeneficiarySearchCritaria schemeBeneficiarySearchCritaria) {
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
                        .build()
        );

        if (existingApplications.isEmpty()) {
            throw new CustomException("BMC_APP_NOT_FOUND", "No scheme application found with the given application number");
        }

        return existingApplications.get(0);
    }

  /*     public void validateWomenEmpowermentScheme(SchemeApplication application) {
        if (!isGenderEligible(application, List.of("Female", "Transgender"))) {
            throw new CustomException("BMC_APP_ERR", "Gender must be Female or Transgender");
        }
        if (!hasValidRationCard(application, List.of("Yellow", "Orange"))) {
            throw new CustomException("BMC_APP_ERR", "Valid ration card with Yellow or Orange category required");
        }

       
        
    }

    public void validateDivyangSelfEmploymentScheme(SchemeApplication application) {
        if (!isGenderEligible(application, List.of("Male", "Female", "Transgender"))) {
            throw new CustomException("BMC_APP_ERR", "Gender must be Male, Female, or Transgender");
        }

       
        if (!hasValidUDID(application, 70)) {
            throw new CustomException("BMC_APP_ERR", "UDID number must have more than 70% disability");
        }

        if (!hasMinimumEducation(application)) {
            throw new CustomException("BMC_APP_ERR", "Minimum 10th pass certificate required from SSC, CBSC, ICSC boards");
        }

      if (hasTakenBenefitInLastFiveYears(application)) {
            throw new CustomException("BMC_APP_ERR", "Already taken benefit in the last five years");
        }  

        
    }

    public void validateDivyangPensionScheme(SchemeApplication application) {
        if (!isGenderEligible(application, List.of("Male", "Female", "Transgender"))) {
            throw new CustomException("BMC_APP_ERR", "Gender must be Male, Female, or Transgender");
        }

    

        // int disabilityPercentage = application.getDisabilityPercentage(); // To be fetched from UDID portal based on UDID number
        // if (disabilityPercentage < 40) {
        //     throw new CustomException("BMC_APP_ERR", "Disability percentage must be more than 40%");
        // }

        // if (disabilityPercentage >= 40 && disabilityPercentage <= 80 ) {
        //     PensionAmount = 12000;
        // }

        // if (disabilityPercentage > 80 ) {
        //     pensionAmount = 36000;
        // }

        
    }

    public void validateWomenSkillDevelopmentScheme(SchemeApplication application) {
       

        if (!hasValidRationCard(application, List.of("Yellow", "Orange"))) {
            throw new CustomException("BMC_APP_ERR", "Valid ration card with Yellow or Orange category required");
        }

        if (!hasMinimumEducation(application)) {
            throw new CustomException("BMC_APP_ERR", "Minimum 10th pass certificate required from SSC, CBSC, ICSC boards");
        }

        
    }

    public void validateDivyangSkillDevelopmentScheme(SchemeApplication application) {
        if (!isAddressFromBMCArea(application)) {
            throw new CustomException("BMC_APP_ERR", "Address must be from BMC area");
        }

        if (!hasValidUDID(application, 70)) {
            throw new CustomException("BMC_APP_ERR", "UDID number must have more than 70% disability");
        }

        if (!hasMinimumEducation(application)) {
            throw new CustomException("BMC_APP_ERR", "Minimum 10th pass certificate required from SSC, CBSC, ICSC boards");
        }

        
    }

    public void validateTransgenderSkillDevelopmentScheme(SchemeApplication application) {
        if (!isAddressFromBMCArea(application)) {
            throw new CustomException("BMC_APP_ERR", "Address must be from BMC area");
        }

        // if (application.getTransgenderRegistrationNumber() == null) {
        //     throw new CustomException("BMC_APP_ERR", "Transgender registration number is required");
        // }

        if (!hasMinimumEducation(application)) {
            throw new CustomException("BMC_APP_ERR", "Minimum 10th pass certificate required from SSC, CBSC, ICSC boards");
        }

        
    }  

    

    private boolean isGenderEligible(User user, List<String> validGenders) {
        return validGenders.contains(user.getGender());
    }

    
    private boolean hasValidRationCard(UserOtherDetails uod, List<String> validCategories) {
        return validCategories.contains(uod.getRationCardCategory());
    }

    private boolean hasValidUDID(UserOtherDetails uod, int requiredDisabilityPercentage) {
        return uod.getUdidNumber() != null && uod.getDivyangPercent() > requiredDisabilityPercentage;
    }

    private boolean hasMinimumEducation(UserOtherDetails uod) {
        String education = uod.getEducationLevel();
        return "SSC".equalsIgnoreCase(education) || "CBSC".equalsIgnoreCase(education) || "ICSC".equalsIgnoreCase(education);
    } */
    

    public EligibilityResponse getBeneficiaryInfo(User user) {
        schemeBeneficiarySearchCritaria.setUserId(user.getId());
        schemeBeneficiarySearchCritaria.setSubmitted(true);
        schemeBeneficiarySearchCritaria.setName("pension");
        
        eligibilityResponse.setMachineTaken(hasTakenMachineInLastFiveYears(schemeBeneficiarySearchCritaria));
        eligibilityResponse.setCourseTaken(hasTakenCourse(schemeBeneficiarySearchCritaria)); 
        eligibilityResponse.setPensionApplied(hasAppliedPension(schemeBeneficiarySearchCritaria)) ;
        
       
        
        return eligibilityResponse; 

    }

    public boolean hasTakenMachineInLastFiveYears(SchemeBeneficiarySearchCritaria critaria) {
        critaria.setForMachine(true);
        List<SchemeBeneficiaryDetails> schemeBeneficiaryDetails = repository.initialEligibilityCheck(schemeBeneficiarySearchCritaria);
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

   
    public boolean hasTakenCourse(SchemeBeneficiarySearchCritaria critaria) {
        critaria.setForCourse(true);
        List<SchemeBeneficiaryDetails> schemeBeneficiaryDetails = repository.initialEligibilityCheck(schemeBeneficiarySearchCritaria);
        Instant startDate = schemeBeneficiaryDetails.get(0).getStartDate();
        Instant endDate = schemeBeneficiaryDetails.get(0).getEndDate();
        if (startDate == null) {
            return false;
        }
        Date lastBenefitDate = Date.from(endDate);
        Date currentDate = new Date();
        long diffInMillis = currentDate.getTime() - lastBenefitDate.getTime();
        long diffInYears = diffInMillis / (1000L * 60 * 60 * 24 * 365);
        return diffInYears <=1;
    } 

    public EligibilityResponse isAddressFromBMCArea(SchemeApplication application) {
        eligibilityResponse.setAddressValidated("BMC".equalsIgnoreCase(application.getAddress().getCity()));
        return eligibilityResponse;
    }

    public boolean hasAppliedPension(SchemeBeneficiarySearchCritaria critaria) {

        critaria.setForPension(true);
        List<SchemeBeneficiaryDetails> schemeBeneficiaryDetails = repository.initialEligibilityCheck(schemeBeneficiarySearchCritaria);
        if(schemeBeneficiaryDetails.get(0).getHas_applied_for_pension() >0){
            return true;
        }

       return false;
    }

    public SchemeBeneficiarySearchCritaria getSchemeBeneficiarySearchCritaria() {
        return schemeBeneficiarySearchCritaria;
    }

}

