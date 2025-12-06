package org.egov.individual.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.helper.RequestInfoTestBuilder;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.service.IdGenService;
import org.egov.individual.config.IndividualProperties;
import org.egov.individual.helper.IndividualBulkRequestTestBuilder;
import org.egov.individual.helper.IndividualTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IndividualEnrichmentServiceTest {

    @InjectMocks
    EnrichmentService enrichmentService;

    @Mock
    IdGenService idGenService;

    @Mock
    IndividualProperties properties;

    @BeforeEach
    void setUp() throws Exception {
        mockIdGen("individual.id", "some-individual-id");
        mockIdGen("sys.gen.identifier.id", "some-sys-gen-id");
        when(properties.getIndividualId()).thenReturn("individual.id");
    }

    private void mockIdGen(String value, String o) throws Exception {
        lenient().when(idGenService.getIdList(any(RequestInfo.class), anyString(),
                        eq(value), eq(null), anyInt()))
                .thenReturn(Collections.singletonList(o));
    }

    @Test
    @DisplayName("should generate address id if address id is null")
    void shouldGenerateAddressIdIfAddressIdIsNull() throws Exception {
         IndividualBulkRequest request = IndividualBulkRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withName()
                        .withTenantId()
                        .withAddress()
                        .build())
                .build();

        enrichmentService.create(request.getIndividuals(), request);

        assertNotNull(request.getIndividuals().get(0)
                .getAddress().stream().findFirst().get()
                .getId());
    }

    @Test
    @DisplayName("should generate skill id if skill id is null")
    void shouldGenerateAddressIdIfSkillIDIsNull() throws Exception {
        IndividualBulkRequest request = IndividualBulkRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withName()
                        .withTenantId()
                        .withAddress()
                        .withSkills()
                        .build())
                .build();

        enrichmentService.create(request.getIndividuals(), request);

        assertNotNull(request.getIndividuals().get(0)
                .getSkills().stream().findFirst().get()
                .getId());
    }
    @Test
    @DisplayName("should generate identifier if not present")
    void shouldGenerateIdentifierIfNotPresent() throws Exception {
        IndividualBulkRequest request = IndividualBulkRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withTenantId()
                        .withName()
                        .build())
                .build();

        enrichmentService.create(request.getIndividuals(), request);

        assertNotNull(request.getIndividuals().get(0)
                .getIdentifiers().stream().findFirst().get()
                .getIdentifierId());
        assertEquals("SYSTEM_GENERATED",
                request.getIndividuals().get(0)
                        .getIdentifiers().stream().findFirst().get()
                        .getIdentifierType());
    }

    @Test
    @DisplayName("should enrich address audit details and individual id if address is not null")
    void shouldEnrichAddressWithAuditDetailsAndIndividualIdIfAddressIsNotNull() throws Exception {
        IndividualBulkRequest request = IndividualBulkRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withName()
                        .withTenantId()
                        .withAddress()
                        .build())
                .build();

        enrichmentService.create(request.getIndividuals(), request);

        assertNotNull(request.getIndividuals().get(0)
                .getAddress().stream().findFirst().get()
                .getId());
        assertNotNull(request.getIndividuals().get(0)
                .getAddress().stream().findFirst().get()
                .getIndividualId());
        assertNotNull(request.getIndividuals().get(0)
                .getAddress().stream().findFirst().get()
                .getAuditDetails());
    }

    @Test
    @DisplayName("should generate individual id")
    void shouldGenerateIndividualId() throws Exception {
        IndividualBulkRequest request = IndividualBulkRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withTenantId()
                        .withName()
                        .build())
                .build();

        enrichmentService.create(request.getIndividuals(), request);

        assertNotNull(request.getIndividuals().get(0)
                        .getId());
    }

    @Test
    @DisplayName("should enrich individuals")
    void shouldEnrichIndividuals() throws Exception {
        IndividualBulkRequest request = IndividualBulkRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withTenantId()
                        .withName()
                        .build())
                .build();

        enrichmentService.create(request.getIndividuals(), request);

        assertNotNull(request.getIndividuals().get(0)
                        .getId());
        assertEquals(1, request.getIndividuals().get(0).getRowVersion());
        assertFalse(request.getIndividuals().get(0).getIsDeleted());
        assertNotNull(request.getIndividuals().get(0).getAuditDetails());
    }



    @Test
    @DisplayName("should enrich identifier with individual id")
    void shouldEnrichIdentifierWithIndividualId() throws Exception {
        IndividualBulkRequest request = IndividualBulkRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withTenantId()
                        .withName()
                        .build())
                .build();

        enrichmentService.create(request.getIndividuals(), request);

        assertNotNull(request.getIndividuals().get(0)
                .getIdentifiers().stream().findFirst().get()
                .getIdentifierId());
        assertEquals("SYSTEM_GENERATED",
                request.getIndividuals().get(0)
                        .getIdentifiers().stream().findFirst().get()
                        .getIdentifierType());
        assertNotNull(request.getIndividuals().get(0)
                        .getIdentifiers().stream().findFirst().get()
                        .getIndividualId());
    }

    @Test
    @DisplayName("should enrich identifiers")
    void shouldEnrichIdentifiers() throws Exception {
        IndividualBulkRequest request = IndividualBulkRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(IndividualTestBuilder.builder()
                        .withTenantId()
                        .withName()
                        .build())
                .build();

        enrichmentService.create(request.getIndividuals(), request);

        assertNotNull(request.getIndividuals().get(0)
                .getIdentifiers().stream().findFirst().get()
                .getIdentifierId());
        assertEquals("SYSTEM_GENERATED",
                request.getIndividuals().get(0)
                        .getIdentifiers().stream().findFirst().get()
                        .getIdentifierType());
        assertNotNull(
                request.getIndividuals().get(0)
                        .getIdentifiers().stream().findFirst().get()
                        .getId());
        assertNotNull(request.getIndividuals().get(0)
                        .getIdentifiers().stream().findFirst().get()
                        .getIdentifierId());
        assertNotNull(request.getIndividuals().get(0)
                .getIdentifiers().stream().findFirst().get()
                .getAuditDetails());
    }
}
