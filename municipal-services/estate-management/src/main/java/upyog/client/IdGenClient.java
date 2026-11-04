package upyog.client;

import org.egov.common.contract.idgen.IdGenerationRequest;
import org.egov.common.contract.idgen.IdGenerationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "idgen-service", url = "${egov.idgen.host}")
public interface IdGenClient {

    @PostMapping(value = "${idgen.service.endpoint}", produces = "application/json", consumes = "application/json")
    IdGenerationResponse getId(@RequestBody IdGenerationRequest request);
}

