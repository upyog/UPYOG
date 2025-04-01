package org.egov.egf.master.web.contract ;
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

public class BankAccountSearchContract extends BankAccountContract { private String ids; 
private String  sortBy; 
private Integer pageSize; 
private Integer offset; 
private List<String> tenantIds;
} 