package org.egov.individual.validators;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.models.Error;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.validator.Validator;
import org.egov.individual.repository.IndividualRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.egov.common.utils.CommonUtils.getEntitiesWithMismatchedRowVersion;
import static org.egov.common.utils.CommonUtils.getIdFieldName;
import static org.egov.common.utils.CommonUtils.getIdMethod;
import static org.egov.common.utils.CommonUtils.getIdToObjMap;
import static org.egov.common.utils.CommonUtils.notHavingErrors;
import static org.egov.common.utils.CommonUtils.populateErrorDetails;
import static org.egov.common.utils.ValidatorUtils.getErrorForRowVersionMismatch;

@Component
@Order(value = 5)
@Slf4j
public class RowVersionValidator implements Validator<IndividualBulkRequest, Individual> {

    private final IndividualRepository individualRepository;

    @Autowired
    public RowVersionValidator(IndividualRepository individualRepository) {
        this.individualRepository = individualRepository;
    }


    @Override
    public Map<Individual, List<Error>> validate(IndividualBulkRequest request) {
        log.info("validating for row version");
        Map<Individual, List<Error>> errorDetailsMap = new HashMap<>();
        Method idMethod = getIdMethod(request.getIndividuals());
        Map<String, Individual> iMap = getIdToObjMap(request.getIndividuals().stream()
                .filter(notHavingErrors())
                .collect(Collectors.toList()), idMethod);
        if (!iMap.isEmpty()) {
            List<String> individualIds = new ArrayList<>(iMap.keySet());
            List<Individual> existingIndividuals = individualRepository.findById(individualIds,
                    getIdFieldName(idMethod), false).getResponse();
            List<Individual> individualsWithMismatchedRowVersion =
                    getEntitiesWithMismatchedRowVersion(iMap, existingIndividuals, idMethod);
            individualsWithMismatchedRowVersion.forEach(individual -> {
                Error error = getErrorForRowVersionMismatch();
                populateErrorDetails(individual, error, errorDetailsMap);
            });
        }
        return errorDetailsMap;
    }
}
