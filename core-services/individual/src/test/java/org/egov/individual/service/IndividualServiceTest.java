package org.egov.individual.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.helper.RequestInfoTestBuilder;
import org.egov.common.models.individual.Address;
import org.egov.common.models.individual.AddressType;
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
import org.egov.individual.validators.UniqueSubEntityValidator;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndividualServiceTest {

    @InjectMocks
    private IndividualService individualService;

    @Mock
    private IdGenService idGenService;

    @Mock
    private IndividualRepository individualRepository;

    @Mock
    private UniqueSubEntityValidator uniqueSubEntityValidator;

    @Mock
    private AddressTypeValidator addressTypeValidator;

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
    void setUp() throws Exception {
        mockIdGen("individual.id", "some-individual-id");
        mockIdGen("sys.gen.identifier.id", "some-sys-gen-id");
        validators = Arrays.asList(addressTypeValidator, uniqueSubEntityValidator);
        ReflectionTestUtils.setField(individualService, "validators", validators);
        when(properties.getSaveIndividualTopic()).thenReturn("save-topic");
    }

    private void mockIdGen(String value, String o) throws Exception {
        lenient().when(idGenService.getIdList(any(RequestInfo.class), anyString(),
                eq(value), eq(null), anyInt()))
                .thenReturn(Collections.singletonList(o));
    }

    @Test
    @DisplayName("should save individuals")
    void shouldSaveIndividuals() throws Exception {
        IndividualRequest request = IndividualRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withTenantId()
                        .withName()
                        .build())
                .build();
        when(encryptionService.encrypt(any(IndividualBulkRequest.class),
                anyList(), any(String.class), anyBoolean())).thenReturn(Collections.singletonList(IndividualTestBuilder.builder()
                .withTenantId()
                .withName()
                .build()));
        lenient().doNothing().when(notificationService).sendNotification(any(IndividualRequest.class),eq(true));
        individualService.create(request);

        verify(individualRepository, times(1))
                .save(anyList(), anyString());
    }

    @Test
    @DisplayName("should validate if only permanent address is present when addresses are not null")
    @Disabled
    void shouldValidateIfOnlyPermanentAddressIsPresentWhenAddressesAreNotNull() {
        Individual individual = IndividualTestBuilder.builder()
                .withTenantId()
                .withName()
                .withAddress()
                .build();

        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(individualService,
                "validateAddressType",
                Collections.singletonList(individual)));
    }

    @Test
    @DisplayName("should throw exception if two permanent addresses are present")
    @Disabled
    void shouldThrowExceptionIfTwoPermanentAddressesArePresent() {
        Individual individual = IndividualTestBuilder.builder()
                .withTenantId()
                .withName()
                .withAddress(Address.builder()
                        .city("some-city")
                        .tenantId("some-tenant-id")
                        .type(AddressType.PERMANENT)
                        .build(), Address.builder()
                        .city("some-city")
                        .tenantId("some-tenant-id")
                        .type(AddressType.PERMANENT)
                        .build())
                .build();

        assertThrows(CustomException.class, () -> ReflectionTestUtils.invokeMethod(individualService,
                "validateAddressType",
                Collections.singletonList(individual)));
    }

    @Test
    @DisplayName("should throw exception if total number of addresses exceed three")
    @Disabled
    void shouldThrowExceptionIfTotalNumberOfAddressesExceedThree() {
        Individual individual = IndividualTestBuilder.builder()
                .withTenantId()
                .withName()
                .withAddress(Address.builder()
                        .city("some-city")
                        .tenantId("some-tenant-id")
                        .type(AddressType.PERMANENT)
                        .build(),
                        Address.builder()
                        .city("some-city")
                        .tenantId("some-tenant-id")
                        .type(AddressType.CORRESPONDENCE)
                        .build(),
                        Address.builder()
                        .city("some-city")
                        .tenantId("some-tenant-id")
                        .type(AddressType.OTHER)
                        .build(),
                        Address.builder()
                        .city("some-city")
                        .tenantId("some-tenant-id")
                        .type(AddressType.CORRESPONDENCE)
                        .build())
                .build();

        assertThrows(CustomException.class, () -> ReflectionTestUtils.invokeMethod(individualService,
                "validateAddressType",
                Collections.singletonList(individual)));
    }
}