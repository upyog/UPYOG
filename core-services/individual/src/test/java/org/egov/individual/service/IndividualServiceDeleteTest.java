package org.egov.individual.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.helper.RequestInfoTestBuilder;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.models.individual.IndividualRequest;
import org.egov.common.service.IdGenService;
import org.egov.common.validator.Validator;
import org.egov.individual.config.IndividualProperties;
import org.egov.individual.helper.IndividualRequestTestBuilder;
import org.egov.individual.helper.IndividualTestBuilder;
import org.egov.individual.repository.IndividualRepository;
import org.egov.individual.validators.NonExistentEntityValidator;
import org.egov.individual.validators.NullIdValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IndividualServiceDeleteTest {

    @InjectMocks
    private IndividualService individualService;

    @Mock
    private IdGenService idGenService;

    @Mock
    private IndividualRepository individualRepository;

    @Mock
    private NullIdValidator nullIdValidator;

    @Mock
    private NonExistentEntityValidator nonExistentEntityValidator;

    private List<Validator<IndividualBulkRequest, Individual>> validators;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private IndividualProperties properties;

    @Mock
    private EnrichmentService enrichmentService;

    @BeforeEach
    void setUp() {
        validators = Arrays.asList(nullIdValidator, nonExistentEntityValidator);
        ReflectionTestUtils.setField(individualService, "validators", validators);
        ReflectionTestUtils.setField(individualService, "isApplicableForDelete",
                (Predicate<Validator<IndividualBulkRequest, Individual>>) validator ->
                        validator.getClass().equals(NullIdValidator.class)
                                || validator.getClass().equals(NonExistentEntityValidator.class));
        lenient().when(properties.getDeleteIndividualTopic()).thenReturn("delete-topic");
    }

    @Test
    @DisplayName("should delete the individual and related entities")
    void shouldDeleteTheIndividualAndRelatedEntities() {
        Individual requestIndividual = IndividualTestBuilder.builder()
                .withId()
                .withClientReferenceId()
                .withRowVersion()
                .withIdentifiers()
                .withAddress()
                .withIsDeleted(true)
                .build();
        IndividualRequest request = IndividualRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(requestIndividual)
                .build();
        List<Individual> individualsInDb = new ArrayList<>();
        individualsInDb.add(IndividualTestBuilder.builder()
                .withClientReferenceId()
                .withId()
                .withName()
                .withTenantId()
                .withAddressHavingAuditDetails()
                .withIdentifiersHavingAuditDetails()
                .withRowVersion()
                .withAuditDetails()
                .build());

        individualService.delete(request);
        verify(individualRepository, times(1)).save(anyList(), anyString());
    }

    @Test
    @DisplayName("should not delete individual when individual has error")
    void shouldNotDeleteIndividualWhenIndividualHasError() {
        Individual individual = IndividualTestBuilder.builder().build();
        individual.setHasErrors(true);
        IndividualRequest request = IndividualRequestTestBuilder.builder()
                .withIndividuals(individual)
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .build();
        List<Individual> individualsInDb = new ArrayList<>();
        individualsInDb.add(IndividualTestBuilder.builder()
                .withClientReferenceId()
                .withId()
                .withName()
                .withTenantId()
                .withAddressHavingAuditDetails()
                .withIdentifiersHavingAuditDetails()
                .withRowVersion()
                .withAuditDetails()
                .build());

        individualService.delete(request);
        verify(individualRepository, times(0)).save(anyList(), anyString());
    }

}
