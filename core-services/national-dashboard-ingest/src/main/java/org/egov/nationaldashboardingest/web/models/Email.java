package org.egov.nationaldashboardingest.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class Email {

    private Set<String> emailTo;
    private String subject;
    private String body;
    @JsonProperty("isHTML")
    private boolean isHTML;
    private Attachments attachments;

}
