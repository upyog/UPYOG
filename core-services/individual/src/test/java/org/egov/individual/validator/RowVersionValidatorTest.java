package org.egov.individual.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.common.models.Error;
import org.egov.common.models.individual.Address;
import org.egov.common.models.individual.AddressType;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.models.individual.Skill;
import org.egov.individual.helper.IndividualBulkRequestTestBuilder;
import org.egov.individual.helper.IndividualTestBuilder;
import org.egov.individual.repository.IndividualRepository;
import org.egov.individual.validators.RowVersionValidator;
import org.egov.individual.web.models.SearchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class RowVersionValidatorTest {

    @InjectMocks
    private RowVersionValidator rowVersionValidator;

    @Mock
    private IndividualRepository individualRepository;

    @Test
    void shouldNotGiveErrorWhenRowVersionMatches() {
        Address address = Address.builder()
                .id("some-Id")
                .city("some-city")
                .tenantId("some-tenant-id")
                .type(AddressType.PERMANENT)
                .isDeleted(false)
                .build();
        Identifier identifier = Identifier.builder()
                .identifierType("SYSTEM_GENERATED")
                .identifierId("some-identifier-id")
                .isDeleted(true)
                .build();
        Skill skill = Skill.builder().type("type").experience("exp").level("lvl").isDeleted(false).build();
        Individual individual = IndividualTestBuilder.builder().withId("some-Id").withAddress(address).withIdentifiers(identifier).withSkills(skill).withRowVersion(1).build();
        IndividualBulkRequest individualBulkRequest = IndividualBulkRequestTestBuilder.builder().withIndividuals(individual).build();
        List<Individual> existingIndividuals = new ArrayList<>();
        existingIndividuals.add(individual);
        lenient().when(individualRepository.findById(anyList(), anyString(), eq(false))).thenReturn(SearchResponse.<Individual>builder()
                .totalCount(Long.valueOf(existingIndividuals.size()))
                .response(existingIndividuals)
                .build());
        assertTrue(rowVersionValidator.validate(individualBulkRequest).isEmpty());

    }

    @Test
    void shouldGiveErrorWhenRowVersionDoesNotMatch() {
        IndividualBulkRequest individualBulkRequest = IndividualBulkRequestTestBuilder.builder()
                .withIndividuals(IndividualTestBuilder.builder()
                        .withId("some-id")
                        .build())
                .build();
        individualBulkRequest.getIndividuals().get(0).setRowVersion(2);
        when(individualRepository.findById(anyList(), anyString(), anyBoolean()))
                .thenReturn(SearchResponse.<Individual>builder().totalCount(1L).response(Collections.singletonList(IndividualTestBuilder.builder()
                        .withId("some-id")
                        .build())).build());
        Map<Individual, List<Error>> errorDetailsMap = new HashMap<>();
        errorDetailsMap = rowVersionValidator.validate(individualBulkRequest);
        List<Error> errorList = errorDetailsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        assertEquals("MISMATCHED_ROW_VERSION", errorList.get(0).getErrorCode());
    }
}
