package digit.web.models.user;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DocumentDetails {
    @JsonProperty("code")
    private Long documentId;
    private Boolean available;
    @JsonProperty("name")
    private String documentName;
    private Long userId;
    private String tenantId;
    private String createdBy;
    private String modifiedBy;
    private Long modifiedOn;
  
    
}