package digit.repository;



import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class SchemeBeneficiarySearchCritaria {

    
    private Long userId;
    private String tenantId; 
    private Boolean submitted;
    private Long optedId;

}


