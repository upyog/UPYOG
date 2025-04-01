package org.egov.individual.helper;


import org.egov.common.models.individual.Gender;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Name;
import org.egov.individual.web.models.IndividualSearch;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class IndividualSearchTestBuilder {
    private IndividualSearch.IndividualSearchBuilder builder;

    public IndividualSearchTestBuilder() {
        this.builder = IndividualSearch.builder();
    }

    public static IndividualSearchTestBuilder builder() {
        return new IndividualSearchTestBuilder();
    }

    public IndividualSearch build() {
        return this.builder.build();
    }

    public IndividualSearchTestBuilder byId(String... args) {
        ArrayList<String> ids = new ArrayList<>();
        if (args != null && args.length > 0) {
            ids.add(args[0]);
        } else {
            ids.add("some-id");
        }
        this.builder.id(ids);
        return this;
    }

    public IndividualSearchTestBuilder byNullId() {
        this.builder.id(null);
        return this;
    }

    public IndividualSearchTestBuilder byClientReferenceId(String... args) {
        ArrayList<String> ids = new ArrayList<>();
        if (args != null && args.length > 0) {
            ids.add(args[0]);
        } else {
            ids.add("some-client-reference-id");
        }

        this.builder.clientReferenceId(ids);
        return this;
    }


    public IndividualSearchTestBuilder byName() {
        this.builder.name(Name.builder()
                        .givenName("some-given-name")
                        .familyName("some-family-name")
                        .otherNames("some-other-name")
                .build());
        return this;
    }

    public IndividualSearchTestBuilder byGender(String... args) {
        this.builder.gender(args != null && args.length > 0 ?
                Gender.valueOf(args[0]) : Gender.MALE);
        return this;
    }

    public IndividualSearchTestBuilder byDateOfBirth(Date... args) {
        this.builder.dateOfBirth(args != null && args.length > 0 ?
                args[0] : Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        return this;
    }

    public IndividualSearchTestBuilder byIdentifier() {
        this.builder.identifier(Identifier.builder()
                        .identifierId("some-identifier-id")
                        .identifierType("SYSTEM_GENERATED")
                .build());
        return this;
    }

    public IndividualSearchTestBuilder byBoundaryCode() {
        this.builder.boundaryCode("some-boundary-code");
        return this;
    }
}
