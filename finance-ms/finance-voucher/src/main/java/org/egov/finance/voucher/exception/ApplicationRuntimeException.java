package org.egov.finance.voucher.exception;

public class ApplicationRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 963479481840349670L;

    public ApplicationRuntimeException(String msg) {
        super(msg);
    }

    public ApplicationRuntimeException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
