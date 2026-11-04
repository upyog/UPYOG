package org.egov.infra.microservice.contract;

import java.util.List;

import org.egov.infra.microservice.models.ChartOfAccounts;
import org.egov.infra.validation.SanitizeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountCodeTemplate {

    private int id;
    @SanitizeHtml
    private String code;
    @SanitizeHtml
    private String name;
    @SanitizeHtml
    private String description;
    @SanitizeHtml
    private String module;
    @SanitizeHtml
    private String subModule;
    @SanitizeHtml
    private String subledgerType;
    private List<ChartOfAccounts> debitCodeDetails;
    private List<ChartOfAccounts> creditCodeDetails;
    private ChartOfAccounts netPayable;
}
