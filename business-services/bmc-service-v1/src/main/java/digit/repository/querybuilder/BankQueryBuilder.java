package digit.repository.querybuilder;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import digit.web.models.bank.BankSearchCriteria;

import java.util.List;

@Component
public class BankQueryBuilder {

    private static final String BASE_QUERY = """
        SELECT b.name AS bankName, bb.branchname AS branchName, bb.ifsc, bb.micr ,bb.id as branchId
        
        """;
    private static final String FROM_TABLES = """
        FROM eg_bmc_bank b
        RIGHT JOIN eg_bmc_bankbranch bb ON b.id = bb.bankid 
            """;
    private static final String BASE_INSERT_QUERY = """
        INSERT INTO eg_bmc_bank 
        (code, name, narration, isactive, type)
        VALUES (?, ?, ?, ?, ?)   
            """;            

     public String getBankDetails(BankSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_QUERY);
        query.append(FROM_TABLES);
        
            if (!ObjectUtils.isEmpty(criteria.getIfsc())) {
                addClauseIfRequired(query, preparedStmtList);
                query.append(" bb.ifsc = ? ");
                preparedStmtList.add(criteria.getIfsc().toUpperCase());
            }
        
        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }
    
    public String getInsertBankQuery() {
        return BASE_INSERT_QUERY.toString();
    }
}
