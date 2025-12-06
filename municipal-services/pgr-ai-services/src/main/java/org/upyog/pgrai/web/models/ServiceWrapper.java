package org.upyog.pgrai.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

<<<<<<< HEAD
import jakarta.validation.Valid;
=======
import javax.validation.Valid;
>>>>>>> master-LTS

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceWrapper {


    @Valid
    @NonNull
    @JsonProperty("service")
    private Service service = null;

    @Valid
    @JsonProperty("workflow")
    private Workflow workflow = null;

}
