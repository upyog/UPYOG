package org.egov.individual.validator;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.models.Error;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.individual.helper.IndividualBulkRequestTestBuilder;
import org.egov.individual.helper.IndividualTestBuilder;
import org.egov.individual.validators.UniqueEntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class UniqueEntityValidatorTest {

    @InjectMocks
    private UniqueEntityValidator uniqueEntityValidator;

    @Test
    void shouldGiveErrorWhenIndividualsWithDuplicateIdsPresent() {
        Individual firstIndividual = IndividualTestBuilder.builder().withId("some-ID").build();
        Individual secondIndividual = IndividualTestBuilder.builder().withId("some-ID").build();
        IndividualBulkRequest individualBulkRequest = IndividualBulkRequestTestBuilder.builder().withIndividuals(firstIndividual, secondIndividual).build();
        Map<Individual, List<Error>> errorDetailsMap = new HashMap<>();
        errorDetailsMap = uniqueEntityValidator.validate(individualBulkRequest);
        List<Error> errorList = errorDetailsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        assertEquals("DUPLICATE_ENTITY", errorList.get(0).getErrorCode());

    }

    @Test
    void shouldNotGiveErrorWhenIndividualsWithUniqueIdsPresent() {
        Individual firstIndividual = IndividualTestBuilder.builder().withId("some-ID").build();
        Individual secondIndividual = IndividualTestBuilder.builder().withId("some-other-ID").build();
        IndividualBulkRequest individualBulkRequest = IndividualBulkRequestTestBuilder.builder().withIndividuals(firstIndividual, secondIndividual).build();
        assertTrue(uniqueEntityValidator.validate(individualBulkRequest).isEmpty());
    }

}
