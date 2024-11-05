package org.egov.nationaldashboardingest.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.Data;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Email {

    private Set<String> emailTo;
    private String subject;
    private String body;
    @JsonProperty("isHTML")
    private boolean isHTML;

}
