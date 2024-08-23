package digit.web.models.employee;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationStatusResponse {

    @JsonProperty("ResponseInfo")
   private ResponseInfo responseInfo;

   private String info;

}
