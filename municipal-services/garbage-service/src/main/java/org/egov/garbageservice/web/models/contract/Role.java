package org.egov.garbageservice.web.models.contract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.egov.tracer.annotations.CustomSafeHtml;


/**
 * Role definition assigned to users for access control in garbage-service integrations.
 * Holds human-readable name, system code, and description; attached to User and TenantRole lists.
 * Unknown JSON properties are ignored to stay compatible with user-service API changes.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    /**
     * -- GETTER --
     * Gets the name.
     *
     * @return the current name
     */
    @NotNull
    @CustomSafeHtml
    private String name;

    /**
     * -- GETTER --
     * Gets the code.
     *
     * @return the current code
     */
    @CustomSafeHtml
    private String code;

    /**
     * -- GETTER --
     * Gets the description.
     *
     * @return the current description
     */
    @CustomSafeHtml
    private String description;

    /**
     * Constructs a new instance with the specified attributes.
     */

    public Role(final String name) {
        this.name = name;
    }
}
