package org.egov.individual.service;

import org.egov.common.helper.RequestInfoTestBuilder;
import org.egov.common.models.individual.Address;
import org.egov.common.models.individual.AddressType;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.models.individual.Skill;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class IndividualEnrichmentServiceUpdateTest {

    @InjectMocks
    EnrichmentService enrichmentService;

    @Mock
    IdGenService idGenService;

    @Mock
    IndividualProperties properties;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("should enrich for update")
    void shouldEnrichForUpdate() throws Exception {
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
                .isDeleted(false)
                .build();
        Skill skill = Skill.builder().id("some-id").type("type").experience("exp").level("lvl").isDeleted(false).build();
        Individual requestIndividual = IndividualTestBuilder.builder()
                .withClientReferenceId()
                .withName("some-new-family-name", "some-new-given-name")
                .withTenantId()
                .withAddress(address)
                .withIdentifiers(identifier)
                .withSkills(skill)
                .withRowVersion()
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
                .withAddress()
                .withRowVersion()
                .withAuditDetails()
                .build());

        enrichmentService.update(request.getIndividuals(), request);

        assertEquals(requestIndividual.getRowVersion(),
                request.getIndividuals().get(0).getRowVersion());
        assertNotNull(request.getIndividuals().get(0).getAuditDetails());
    }

}
