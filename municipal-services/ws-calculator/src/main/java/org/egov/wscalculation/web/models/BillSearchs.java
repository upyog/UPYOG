package org.egov.wscalculation.web.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BillSearchs {

  
    private String consumercode;

    public String getConsumercode() {
        return consumercode;
    }

    public void setConsumercode(String consumercode) {
        this.consumercode = consumercode;
    }
}
	
