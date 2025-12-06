package org.egov.individual.validators;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.models.Error;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.validator.Validator;
import org.egov.individual.config.IndividualProperties;
import org.egov.individual.repository.IndividualRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.egov.common.utils.CommonUtils.isValidPattern;
import static org.egov.common.utils.CommonUtils.populateErrorDetails;

@Component
@Slf4j
public class MobileNumberValidator implements Validator<IndividualBulkRequest, Individual> {

    private final IndividualRepository individualRepository;

    private final IndividualProperties properties;

    @Autowired
    public MobileNumberValidator(IndividualRepository individualRepository, IndividualProperties properties) {
        this.individualRepository = individualRepository;
        this.properties = properties;
    }

    @Override
    public Map<Individual, List<Error>> validate(IndividualBulkRequest request) {
        log.info("validating mobileNumber");
        Map<Individual, List<Error>> errorDetailsMap = new HashMap<>();
        List<Individual> individuals = request.getIndividuals();

        if (!individuals.isEmpty()) {
            for (Individual individual : individuals) {
                //check mobile number has all numbers , if present
                if (StringUtils.isNotBlank(individual.getMobileNumber())
                        && !isValidPattern(individual.getMobileNumber(),properties.getMobilePattern())) {
                    Error error = Error.builder().errorMessage("Invalid MobileNumber").errorCode("INVALID_MOBILENUMBER").type(Error.ErrorType.NON_RECOVERABLE).exception(new CustomException("INVALID_MOBILENUMBER", "Invalid MobileNumber")).build();
                    populateErrorDetails(individual, error, errorDetailsMap);
                }
            }

        }
        return errorDetailsMap;
    }


}
