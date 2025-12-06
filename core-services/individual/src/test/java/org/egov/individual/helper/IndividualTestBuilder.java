package org.egov.individual.helper;

import org.egov.common.helper.AuditDetailsTestBuilder;
import org.egov.common.models.individual.Address;
import org.egov.common.models.individual.AddressType;
import org.egov.common.models.individual.Gender;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.Name;
import org.egov.common.models.individual.Skill;

import java.util.Arrays;

public class IndividualTestBuilder {
    private Individual.IndividualBuilder builder;

    public IndividualTestBuilder() {
        this.builder = Individual.builder();
    }

    public static IndividualTestBuilder builder() {
        return new IndividualTestBuilder();
    }

    public Individual build() {
        this.builder.gender(Gender.MALE).hasErrors(false);
        return this.builder.build();
    }

    public IndividualTestBuilder withName(String... args) {
        this.builder.name(Name.builder()
                .familyName(args.length > 0 && args[0] != null ? args[0] : "some-family-name")
                .givenName(args.length > 1 && args[1] != null ? args[1] : "some-given-name")
                .build());
        return this;
    }

    public IndividualTestBuilder withId(String... args) {
        this.builder.id(args.length > 0 ? args[0] : "some-id");
        return this;
    }

    public IndividualTestBuilder withTenantId(String... args) {
        this.builder.tenantId(args.length > 0 ? args[0] : "some-tenant-id");
        return this;
    }

    public IndividualTestBuilder withNoPropertiesSet(String... args) {
        this.builder.build();
        return this;
    }

    public IndividualTestBuilder withAddress(Address... addresses) {
        Address address = Address.builder()
                .city("some-city")
                .tenantId("some-tenant-id")
                .type(AddressType.PERMANENT)
                .isDeleted(false)
                .build();
        if (addresses != null && addresses.length > 0) {
            this.builder.address(Arrays.asList(addresses));
            return this;
        }
        this.builder.address(Arrays.asList(address));
        return this;
    }

    public IndividualTestBuilder withAddressHavingAuditDetails(Address... addresses) {
        Address address = Address.builder()
                .city("some-city")
                .tenantId("some-tenant-id")
                .type(AddressType.PERMANENT)
                .auditDetails(AuditDetailsTestBuilder.builder()
                        .withAuditDetails()
                        .build())
//                .rowVersion(1)
                .build();
        if (addresses != null && addresses.length > 0) {
            this.builder.address(Arrays.asList(addresses));
            return this;
        }
        this.builder.address(Arrays.asList(address));
        return this;
    }

    public IndividualTestBuilder withIsDeleted(boolean isDeleted) {
        this.builder.isDeleted(isDeleted);
        return this;
    }

    public IndividualTestBuilder withClientReferenceId(String... args) {
        this.builder.clientReferenceId(args.length > 0 ? args[0] : "some-client-reference-id");
        return this;
    }

    public IndividualTestBuilder withRowVersion(int... args) {
        this.builder.rowVersion(args.length > 0 ? args[0] : 1);
        return this;
    }

    public IndividualTestBuilder withAuditDetails() {
        this.builder.auditDetails(AuditDetailsTestBuilder.builder()
                        .withAuditDetails()
                .build());
        return this;
    }

    public IndividualTestBuilder withIdentifiers(Identifier... identifiers) {
        Identifier identifier = Identifier.builder()
                .identifierType("SYSTEM_GENERATED")
                .identifierId("some-identifier-id")
                .isDeleted(false)
                .build();
        if (identifiers != null && identifiers.length > 0) {
            this.builder.identifiers(Arrays.asList(identifiers));
            return this;
        }
        this.builder.identifiers(Arrays.asList(identifier));
        return this;
    }

    public IndividualTestBuilder withSkills(Skill... skills) {
        Skill skill = Skill.builder().type("type").experience("exp").level("lvl").build();
        if (skills != null && skills.length > 0) {
            this.builder.skills(Arrays.asList(skills));
            return this;
        }
        this.builder.skills(Arrays.asList(skill));
        return this;
    }

    public IndividualTestBuilder withIdentifiersHavingAuditDetails(Identifier... identifiers) {
        Identifier identifier = Identifier.builder()
                .identifierType("SYSTEM_GENERATED")
                .identifierId("some-identifier-id")
                .auditDetails(AuditDetailsTestBuilder.builder()
                        .withAuditDetails()
                        .build())
//                .rowVersion(1)
                .build();
        if (identifiers != null && identifiers.length > 0) {
            this.builder.identifiers(Arrays.asList(identifiers));
            return this;
        }
        this.builder.identifiers(Arrays.asList(identifier));
        return this;
    }
}
