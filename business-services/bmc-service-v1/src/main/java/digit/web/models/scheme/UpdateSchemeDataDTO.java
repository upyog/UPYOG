package digit.web.models.scheme;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import digit.web.models.user.DocumentDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSchemeDataDTO {


        
        @JsonProperty("documents")
        private List<DocumentDetails> documents;

        @JsonProperty("income")
        private IncomeDTO income;

        @JsonProperty("agreeToPay")
        private boolean agreeToPay;

        @JsonProperty("statement")
        private boolean statement;

        @JsonProperty("Occupation")
        private OccupationDTO occupation;


}
