package org.upyog.adv.web.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.upyog.adv.web.models.Error;
import org.upyog.adv.web.models.ResponseInfo;
import org.springframework.validation.annotation.Validated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * All APIs will return ErrorRes in case of failure which will carry ResponseInfo as metadata and Error object as actual representation of error. In case of bulk apis, some apis may chose to return the array of Error objects to indicate individual failure.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorRes   {
        @JsonProperty("ResponseInfo")
        private ResponseInfo responseInfo = null;

        @JsonProperty("Errors")
        @Valid
        private List<Error> errors = null;


        public ErrorRes addErrorsItem(Error errorsItem) {
            if (this.errors == null) {
            this.errors = new ArrayList<>();
            }
        this.errors.add(errorsItem);
        return this;
        }

}

