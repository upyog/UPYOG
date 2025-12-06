package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

<<<<<<< HEAD
import jakarta.validation.Valid;
=======
import javax.validation.Valid;
>>>>>>> master-LTS
import java.util.ArrayList;
import java.util.List;

/**
 * Boundary
 */
@Validated
<<<<<<< HEAD
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
=======
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Boundary {
    @JsonProperty("code")
    private String code = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("label")
    private String label = null;

    @JsonProperty("latitude")
    private String latitude = null;

    @JsonProperty("longitude")
    private String longitude = null;

    @JsonProperty("children")
    @Valid
    private List<Boundary> children = null;

    @JsonProperty("materializedPath")
    private String materializedPath = null;


    public Boundary addChildrenItem(Boundary childrenItem) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(childrenItem);
        return this;
    }

}

