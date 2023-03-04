package org.ksmart.birth.common.contract;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthResponse   {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
        @JsonProperty("ResponseInfo")
        private ResponseInfo responseInfo = null;

        @JsonProperty("birthDetails")
        @Valid
        private List<NewBirthApplication> birthDetails = null;

}

