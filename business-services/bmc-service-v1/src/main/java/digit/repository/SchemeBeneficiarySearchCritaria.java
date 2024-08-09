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
    private Boolean submitted;
    private String optedId;
    private String name;
    private int has_applied_for_pension;
    private Boolean forMachine;
    private Boolean forCourse;
    private Boolean forPension;

    


}


