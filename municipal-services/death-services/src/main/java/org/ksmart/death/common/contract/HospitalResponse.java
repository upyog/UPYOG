package org.ksmart.death.common.contract;

import java.util.List;

import javax.validation.Valid;

import org.ksmart.death.common.model.EgHospitalDtl;
import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HospitalResponse   {
        @JsonProperty("ResponseInfo")
        private ResponseInfo responseInfo = null;

        @JsonProperty("hospitalDtls")
        @Valid
        private List<EgHospitalDtl> hospitalDtls = null;

}

