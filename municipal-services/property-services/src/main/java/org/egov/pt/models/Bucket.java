package org.egov.pt.models;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Bucket {
    private String name;
    private BigDecimal value;

}
