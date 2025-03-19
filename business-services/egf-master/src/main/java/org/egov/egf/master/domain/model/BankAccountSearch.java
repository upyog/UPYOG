package org.egov.egf.master.domain.model ;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class BankAccountSearch extends BankAccount{ private String ids; 
private String  sortBy; 
private Integer pageSize; 
private Integer offset; 
private List<String> tenantIds;
} 