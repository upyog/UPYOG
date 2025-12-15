package org.egov.tobacco.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDetails {
    private String name;
    private String village;
    private String town;
    private String city;
    private String block;
    private String district;
    private String state;
    private String pinCode;
    private String telephone;
    private String mobile;
    private String email;
}
