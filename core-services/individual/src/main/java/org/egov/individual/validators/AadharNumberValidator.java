package org.egov.individual.validators;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.models.Error;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.validator.Validator;
import org.egov.individual.config.IndividualProperties;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.egov.common.utils.CommonUtils.isValidPattern;
import static org.egov.common.utils.CommonUtils.populateErrorDetails;

@Component
@Slf4j
public class AadharNumberValidator implements Validator<IndividualBulkRequest, Individual> {

    private final IndividualProperties properties;

    @Autowired
    public AadharNumberValidator(IndividualProperties properties) {
        this.properties = properties;
    }

    @Override
    public Map<Individual, List<Error>> validate(IndividualBulkRequest request) {
        log.info("validating aadhaarNumber");
        Map<Individual, List<Error>> errorDetailsMap = new HashMap<>();
        List<Individual> individuals = request.getIndividuals();

        if (!individuals.isEmpty()) {
            for (Individual individual : individuals) {
               if (!CollectionUtils.isEmpty(individual.getIdentifiers())) {
                   Identifier identifier = individual.getIdentifiers().stream()
                           .filter(id -> id.getIdentifierType().contains("AADHAAR"))
                           .findFirst().orElse(null);
                   if (identifier != null && StringUtils.isNotBlank(identifier.getIdentifierId())) {
                       if (!identifier.getIdentifierId().contains("*")
                               && !isValidPattern(identifier.getIdentifierId(), properties.getAadhaarPattern())) {
                           createError(errorDetailsMap, individual);
                       } else if (identifier.getIdentifierId().contains("*")) {
                           // get the last 4 digits
                           String last4Digits = identifier.getIdentifierId()
                                   .substring(identifier.getIdentifierId().length() - 4);
                           // regex to check if last 4 digits are numbers
                           String regex = "[0-9]+";
                           if (!isValidPattern(last4Digits, regex) || identifier.getIdentifierId().length() != 12) {
                               createError(errorDetailsMap, individual);
                           }
                       }
                   }
               }
            }
        }
        return errorDetailsMap;
    }

    private static void createError(Map<Individual, List<Error>> errorDetailsMap, Individual individual) {
        Error error = Error.builder().errorMessage("Invalid Aadhaar").errorCode("INVALID_AADHAAR")
                  .type(Error.ErrorType.NON_RECOVERABLE)
                  .exception(new CustomException("INVALID_AADHAAR", "Invalid Aadhaar")).build();
        populateErrorDetails(individual, error, errorDetailsMap);
    }


}
