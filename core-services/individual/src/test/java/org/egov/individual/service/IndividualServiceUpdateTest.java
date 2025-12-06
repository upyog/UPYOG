package org.egov.individual.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.egov.common.helper.RequestInfoTestBuilder;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.models.individual.IndividualRequest;
import org.egov.common.service.IdGenService;
import org.egov.common.validator.Validator;
import org.egov.individual.config.IndividualProperties;
import org.egov.individual.helper.IndividualRequestTestBuilder;
import org.egov.individual.helper.IndividualTestBuilder;
import org.egov.individual.repository.IndividualRepository;
import org.egov.individual.validators.AddressTypeValidator;
import org.egov.individual.validators.IsDeletedSubEntityValidator;
import org.egov.individual.validators.IsDeletedValidator;
import org.egov.individual.validators.NonExistentEntityValidator;
import org.egov.individual.validators.NullIdValidator;
import org.egov.individual.validators.RowVersionValidator;
import org.egov.individual.validators.UniqueEntityValidator;
import org.egov.individual.validators.UniqueSubEntityValidator;
import org.egov.individual.web.models.SearchResponse;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class IndividualServiceUpdateTest {

    @InjectMocks
    private IndividualService individualService;

    @Mock
    private IdGenService idGenService;

    @Mock
    private IndividualRepository individualRepository;

    @Mock
    private AddressTypeValidator addressTypeValidator;

    @Mock
    private NullIdValidator nullIdValidator;

    @Mock
    private NonExistentEntityValidator nonExistentEntityValidator;

    @Mock
    private UniqueSubEntityValidator uniqueSubEntityValidator;

    @Mock
    private UniqueEntityValidator uniqueEntityValidator;

    @Mock
    private IsDeletedValidator isDeletedValidator;

    @Mock
    private IsDeletedSubEntityValidator isDeletedSubEntityValidator;

    @Mock
    private RowVersionValidator rowVersionValidator;

    @Mock
    private IndividualProperties properties;

    @Mock
    private EnrichmentService enrichmentService;

    @Mock
    private IndividualEncryptionService encryptionService;

    @Mock
    private NotificationService notificationService;

    private List<Validator<IndividualBulkRequest, Individual>> validators;

    @BeforeEach
    void setUp() {
        validators = Arrays.asList(addressTypeValidator, nullIdValidator, nonExistentEntityValidator,
                uniqueEntityValidator, uniqueSubEntityValidator,
                rowVersionValidator, isDeletedSubEntityValidator, isDeletedValidator);
        ReflectionTestUtils.setField(individualService, "validators", validators);
        lenient().when(properties.getUpdateIndividualTopic()).thenReturn("update-topic");
    }

    @Test
    @DisplayName("should throw exception if ids are null")
    @Disabled
    void shouldThrowExceptionIfIdsAreNull() {
        IndividualRequest request = IndividualRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withName()
                        .withTenantId()
                        .withAddress()
                        .build())
                .build();

        assertThrows(CustomException.class, () -> individualService.update(request));
    }

    @Test
    @DisplayName("should throw exception if entities do not exist in db")
    @Disabled
    void shouldThrowExceptionIfEntitiesDoNotExistInDb() {
        IndividualRequest request = IndividualRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withClientReferenceId()
                        .withName()
                        .withTenantId()
                        .withAddress()
                        .build())
                .build();

        assertThrows(CustomException.class, () -> individualService.update(request));
    }

    @Test
    @DisplayName("should check row versions if entities are valid")
    void shouldCheckRowVersionsIfEntitiesAreValid() {
        Individual requestIndividual = IndividualTestBuilder.builder()
                .withClientReferenceId()
                .withId("some-id")
                .withName()
                .withTenantId()
                .withAddress()
                .withIdentifiers(Identifier.builder()
                        .id("some-id")
                        .identifierId("some-id")
                        .identifierType("some-type").build())
                .withRowVersion()
                .build();
        IndividualRequest request = IndividualRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(requestIndividual)
                .build();
        List<Individual> individualsInDb = new ArrayList<>();
        individualsInDb.add(IndividualTestBuilder.builder()
                .withClientReferenceId()
                .withId("some-id")
                .withName()
                .withTenantId()
                .withAddress()
                .withIdentifiers(Identifier.builder()
                        .id("some-id")
                        .individualId("some-id")
                        .identifierId("some-id")
                        .identifierType("some-type").build())
                .withRowVersion()
                .withAuditDetails()
                .build());


        when(individualRepository.findById(anyList(),eq("id"),eq(false))).thenReturn(SearchResponse.<Individual>builder()
                .totalCount(Long.valueOf(individualsInDb.size()))
                .response(individualsInDb)
                .build());
        when(encryptionService.encrypt(any(IndividualBulkRequest.class),
                anyList(), any(String.class), anyBoolean())).thenReturn(Collections.singletonList(requestIndividual));

        lenient().doNothing().when(notificationService).sendNotification(any(IndividualRequest.class),eq(false));

        assertDoesNotThrow(() -> individualService.update(request));
    }

    @Test
    @DisplayName("should throw exception if row versions do not match")
    @Disabled
    void shouldThrowExceptionIfRowVersionsDoNotMatch() {
        IndividualRequest request = IndividualRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withClientReferenceId()
                        .withName()
                        .withTenantId()
                        .withAddress()
                        .withRowVersion(2)
                        .build())
                .build();
        List<Individual> individualsInDb = new ArrayList<>();
        individualsInDb.add(IndividualTestBuilder.builder()
                .withClientReferenceId()
                .withId()
                .withName()
                .withTenantId()
                .withAddress()
                .withRowVersion()
                .build());

        assertThrows(CustomException.class, () -> individualService.update(request));
    }

    @Test
    @DisplayName("should save the updated entities")
    void shouldSaveTheUpdatedEntities() {
        Individual requestIndividual = IndividualTestBuilder.builder()
                .withClientReferenceId()
                .withName("some-new-family-name", "some-new-given-name")
                .withTenantId()
                .withAddress()
                .withId("some-id")
                .withIdentifiers(Identifier.builder()
                        .id("some-id")
                        .identifierId("some-id")
                        .identifierType("some-type").build())
                .withRowVersion()
                .build();
        IndividualRequest request = IndividualRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(requestIndividual)
                .build();
        List<Individual> individualsInDb = new ArrayList<>();
        individualsInDb.add(IndividualTestBuilder.builder()
                .withClientReferenceId()
                .withId("some-id")
                .withName()
                .withTenantId()
                .withAddress()
                .withIdentifiers(Identifier.builder()
                        .id("some-id")
                        .individualId("some-id")
                        .identifierId("some-id")
                        .identifierType("some-type").build())
                .withRowVersion()
                .withAuditDetails()
                .build());

        when(individualRepository.findById(anyList(),eq("id"),eq(false))).thenReturn(SearchResponse.<Individual>builder()
                .totalCount(Long.valueOf(individualsInDb.size()))
                .response(individualsInDb)
                .build());
        when(encryptionService.encrypt(any(IndividualBulkRequest.class),
                anyList(), any(String.class), anyBoolean())).thenReturn(Collections.singletonList(requestIndividual));
        List<Individual> result = individualService.update(request);
        verify(individualRepository, times(1)).save(anyList(), anyString());
    }

}
