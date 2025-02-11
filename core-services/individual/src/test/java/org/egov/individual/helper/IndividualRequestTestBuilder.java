package org.egov.individual.helper;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.helper.RequestInfoTestBuilder;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualRequest;


public class IndividualRequestTestBuilder {
    private IndividualRequest.IndividualRequestBuilder builder;

    public IndividualRequestTestBuilder() {
        this.builder = IndividualRequest.builder();
    }

    public static IndividualRequestTestBuilder builder() {
        return new IndividualRequestTestBuilder();
    }

    public IndividualRequest build() {
        return this.builder.build();
    }

    public IndividualRequestTestBuilder withIndividuals(Individual individual) {
        this.builder.individual(individual);
        return this;
    }

    public IndividualRequestTestBuilder withRequestInfo(RequestInfo... args) {
        this.builder.requestInfo(args.length > 0 ? args[0] :
                RequestInfoTestBuilder.builder().withCompleteRequestInfo().build());
        return this;
    }

//    public IndividualRequestTestBuilder withApiOperation(ApiOperation apiOperation) {
//        this.builder.apiOperation(apiOperation);
//        return this;
//    }
}
