package org.egov.dx.web.models;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XStreamAlias("WaterSewerageBill")
public class WaterSewerageBill {

    private String consumerNo;
    private String consumerName;
    private String mobileNumber;
    private String address;
    private String billNumber;
    private String billDate;
    private String billAmount;
    private String status;

}