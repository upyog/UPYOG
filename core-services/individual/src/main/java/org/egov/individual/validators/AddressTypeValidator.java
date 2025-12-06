package org.egov.individual.validators;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.models.Error;
import org.egov.common.models.individual.Address;
import org.egov.common.models.individual.AddressType;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.validator.Validator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.egov.common.utils.CommonUtils.populateErrorDetails;
import static org.egov.common.utils.ValidatorUtils.getErrorForAddressType;

@Component
@Order(value = 2)
@Slf4j
public class AddressTypeValidator implements Validator<IndividualBulkRequest, Individual> {

    @Override
    public Map<Individual, List<Error>> validate(IndividualBulkRequest request) {
        log.info("validating address type");
        Map<Individual, List<Error>> errorDetailsMap = new HashMap<>();
        List<Individual> individuals = request.getIndividuals();
        if (!individuals.isEmpty()) {
            List<Individual> individualsWithInvalidAddress = validateAddressType(individuals);
            individualsWithInvalidAddress.forEach(individual -> {
                Error error = getErrorForAddressType();
                populateErrorDetails(individual, error, errorDetailsMap);
            });
        }
        return errorDetailsMap;
    }

    private List<Individual> validateAddressType(List<Individual> indInReq) {
        List<Individual> individuals = new ArrayList<>();
        for (Individual individual : indInReq) {
            Map<AddressType, Integer> addressTypeCountMap = new EnumMap<>(AddressType.class);
            if (individual.getAddress() == null) {
                continue;
            }
            for (Address address : individual.getAddress()) {
                addressTypeCountMap.merge(address.getType(), 1, Integer::sum);
            }
            addressTypeCountMap.entrySet().stream().filter(e -> e.getValue() > 1)
                    .forEach(e -> individuals.add(individual));
        }
        return individuals;
    }
}
