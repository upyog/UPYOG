package com.example.hpgarbageservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class GrbgDocument {

    private String uuid;
    private String docRefId;
    private String docName;
    private String docType;
    private String docCategory;
    private String tblRefUuid;
}
