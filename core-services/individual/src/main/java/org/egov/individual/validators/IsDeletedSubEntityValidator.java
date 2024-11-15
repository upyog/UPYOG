package org.egov.individual.validators;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.models.Error;
import org.egov.common.models.individual.Address;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.models.individual.Skill;
import org.egov.common.validator.Validator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.egov.common.utils.CommonUtils.populateErrorDetails;
import static org.egov.common.utils.ValidatorUtils.getErrorForIsDeleteSubEntity;

@Component
@Order(2)
@Slf4j
public class IsDeletedSubEntityValidator  implements Validator<IndividualBulkRequest, Individual> {

    @Override
    public Map<Individual, List<Error>> validate(IndividualBulkRequest request) {
        log.info("validating sub entities isDeleted field");
        HashMap<Individual, List<Error>> errorDetailsMap = new HashMap<>();
        List<Individual> validIndividuals = request.getIndividuals();
        for (Individual individual : validIndividuals) {
            List<Identifier> identifiers = individual.getIdentifiers();
            if (identifiers != null) {
                log.info("validating sub entities isDeleted field for identifiers");
                identifiers.stream().filter(Identifier::getIsDeleted)
                        .forEach(identifier -> {
                            Error error = getErrorForIsDeleteSubEntity();
                            populateErrorDetails(individual, error, errorDetailsMap);
                        });
            }

            List<Address> addresses = individual.getAddress();
            if (addresses != null) {
                log.info("validating sub entities isDeleted field for address");
                addresses.stream().filter(Address::getIsDeleted)
                        .forEach(address -> {
                            Error error = getErrorForIsDeleteSubEntity();
                            populateErrorDetails(individual, error, errorDetailsMap);
                        });
            }

            List<Skill> skills = individual.getSkills();
            if (skills != null) {
                log.info("validating sub entities isDeleted field for skills");
                skills.stream().filter(Skill::getIsDeleted)
                        .forEach(skill -> {
                            Error error = getErrorForIsDeleteSubEntity();
                            populateErrorDetails(individual, error, errorDetailsMap);
                        });
            }
        }
        return errorDetailsMap;
    }
}
