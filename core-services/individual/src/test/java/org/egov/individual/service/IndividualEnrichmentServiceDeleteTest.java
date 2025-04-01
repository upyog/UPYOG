package org.egov.individual.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.helper.RequestInfoTestBuilder;
import org.egov.common.models.individual.Address;
import org.egov.common.models.individual.AddressType;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.models.individual.Skill;
import org.egov.common.service.IdGenService;
import org.egov.individual.helper.IndividualBulkRequestTestBuilder;
import org.egov.individual.helper.IndividualTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class IndividualEnrichmentServiceDeleteTest {

    @InjectMocks
    private EnrichmentService enrichmentService;

    @Mock
    private IdGenService idGenService;

    @Mock
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("should delete the individual and related entities")
    void shouldDeleteTheIndividualAndRelatedEntities() throws Exception {
        Individual requestIndividual = IndividualTestBuilder.builder()
                .withId()
                .withClientReferenceId()
                .withRowVersion()
                .withIdentifiers()
                .withAddress()
                .withSkills()
                .withIsDeleted(true)
                .build();
        IndividualBulkRequest request = IndividualBulkRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(requestIndividual)
                .build();
        List<Individual> individualsInDb = new ArrayList<>();
        individualsInDb.add(IndividualTestBuilder.builder()
                .withClientReferenceId()
                .withId()
                .withName()
                .withTenantId()
                .withSkills()
                .withAddressHavingAuditDetails()
                .withIdentifiersHavingAuditDetails()
                .withRowVersion()
                .withAuditDetails()
                .build());

        enrichmentService.delete(request.getIndividuals(), request);

        assertEquals(requestIndividual.getRowVersion(),
                request.getIndividuals().get(0).getRowVersion());
        assertNotNull(request.getIndividuals().get(0).getAuditDetails());
        assertTrue(request.getIndividuals().get(0).getAddress().stream().findFirst().get().getIsDeleted());
        assertTrue(request.getIndividuals().get(0).getIdentifiers().stream().findFirst().get().getIsDeleted());
        assertTrue(request.getIndividuals().get(0).getSkills().stream().findFirst().get().getIsDeleted());
        assertEquals(2, request.getIndividuals().get(0).getRowVersion());
    }

    @Test
    void shouldDeleteIndividualWhenRelatedEntitiesAreDeleted() throws Exception {
        Address address = Address.builder()
                .id("some-Id")
                .city("some-city")
                .tenantId("some-tenant-id")
                .type(AddressType.PERMANENT)
                .isDeleted(true)
                .build();
        Identifier identifier = Identifier.builder()
                .identifierType("SYSTEM_GENERATED")
                .identifierId("some-identifier-id")
                .isDeleted(true)
                .build();
        Skill skill = Skill.builder().id("some-id").type("type").experience("exp").level("lvl").isDeleted(true).build();
        Individual requestIndividual = IndividualTestBuilder.builder()
                .withId()
                .withClientReferenceId()
                .withRowVersion()
                .withIdentifiers(identifier)
                .withAddress(address)
                .withSkills(skill)
                .withIsDeleted(false)
                .withAuditDetails()
                .build();
        IndividualBulkRequest request = IndividualBulkRequestTestBuilder.builder()
                .withRequestInfo(RequestInfoTestBuilder.builder().withCompleteRequestInfo().build())
                .withIndividuals(requestIndividual)
                .build();
        List<Individual> individualsInDb = new ArrayList<>();
        individualsInDb.add(requestIndividual);
        enrichmentService.delete(request.getIndividuals(), request);

        assertEquals(2, request.getIndividuals().get(0).getRowVersion());


    }
}
