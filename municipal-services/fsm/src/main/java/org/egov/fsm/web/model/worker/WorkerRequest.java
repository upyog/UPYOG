package org.egov.fsm.web.model.worker;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;


@Validated
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class WorkerRequest {


  @JsonProperty("workers")
  private List<Worker> workers;
}
