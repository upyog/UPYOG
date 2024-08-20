package com.example.hpgarbageservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(exclude = {"uuid"})
public class GrbgApplication {

    private String uuid;
    private String applicationNo;
    private String status;
    private Long garbageId;
}
