package org.egov.individual.helper;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.helper.RequestInfoTestBuilder;
import org.egov.common.models.individual.Gender;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Name;
import org.egov.individual.web.models.IndividualSearch;
import org.egov.individual.web.models.IndividualSearchRequest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class IndividualSearchRequestTestBuilder {
    private IndividualSearchRequest.IndividualSearchRequestBuilder builder;

    public IndividualSearchRequestTestBuilder() {
        this.builder = IndividualSearchRequest.builder();
    }

    public static IndividualSearchRequestTestBuilder builder() {
        return new IndividualSearchRequestTestBuilder();
    }

    public IndividualSearchRequest build() {
        return this.builder.build();
    }

    public IndividualSearchRequestTestBuilder withIndividualSearch(IndividualSearch... args) {
        if (args != null && args.length > 0) {
            this.builder.individual(args[0]).build();
            return this;
        }
        ArrayList<String> ids = new ArrayList<>();
        ids.add("some-id");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1988);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        this.builder.individual(IndividualSearch.builder()
                        .id(ids)
                        .name(Name.builder()
                                .givenName("some-given-name")
                                .build())
                        .dateOfBirth(Date.from(LocalDate.of(2023, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                        .boundaryCode("some-boundary-code")
                        .gender(Gender.MALE)
                        .identifier(Identifier.builder()
                                .identifierType("SYSTEM_GENERATED")
                                .identifierId("some-identifier-id")
                                .build())
                .build());
        return this;
    }

    public IndividualSearchRequestTestBuilder withRequestInfo(RequestInfo... args) {
        this.builder.requestInfo(args.length > 0 ? args[0] :
                RequestInfoTestBuilder.builder().withCompleteRequestInfo().build());
        return this;
    }
}
