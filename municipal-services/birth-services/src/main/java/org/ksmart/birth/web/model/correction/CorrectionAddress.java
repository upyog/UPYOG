package org.ksmart.birth.web.model.correction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CorrectionAddress {

    @Size(max = 64)
    @JsonProperty("permanentUuid")
    private String permanentUuid;

    @Size(max = 2500)
    @JsonProperty("permanentLocalityNameEn")
    private String permanentLocalityNameEn;

    @Size(max = 2500)
    @JsonProperty("permanentStreetNameEn")
    private String permanentStreetNameEn;

    @Size(max = 2500)
    @JsonProperty("permanentHouseNameEn")
    private String permanentHouseNameEn;

    @Size(max = 2500)
    @JsonProperty("permanentLocalityNameMl")
    private String permanentLocalityNameMl;

    @Size(max = 2500)
    @JsonProperty("permanentStreetNameMl")
    private String permanentStreetNameMl;

    @Size(max = 2500)
    @JsonProperty("permanentHouseNameMl")
    private String permanentHouseNameMl;



}































