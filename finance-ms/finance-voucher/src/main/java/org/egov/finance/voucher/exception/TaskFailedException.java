package org.egov.finance.voucher.exception;

public class TaskFailedException extends Exception {

	/**
	*
	*/
	private static final long serialVersionUID = 2537268816077025966L;

	public TaskFailedException() {
		super();
	}

	public TaskFailedException(final String arg0) {
		super(arg0);
	}

	public TaskFailedException(final Throwable arg0) {
		super(arg0);
	}

	public TaskFailedException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

}
