package org.egov.egf.contract.model;

import java.io.Serializable;

import org.egov.infra.validation.SanitizeHtml;

public class BankAccount implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5118183614058884219L;
    @SanitizeHtml
    private String code;
    @SanitizeHtml
    private String account;

    public BankAccount(final String code, final String account) {
        this.code = code;
        this.account = account;
    }

    public BankAccount() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

}
