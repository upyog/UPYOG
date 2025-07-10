package org.egov.finance.voucher.exception;

import java.io.Serializable;

public class ValidationError implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String key;
	private final String message;
	private String[] args = null;
	private boolean nonFieldError = false;

	public ValidationError(final String key, final String message) {
		this.key = key;
		this.message = message;
	}

	public ValidationError(String key, String message, boolean nonFieldError) {
		this(key, message);
		this.nonFieldError = nonFieldError;
	}

	/**
	 * Gets a message based on a key using the supplied args, as defined in
	 * {@link java.text.MessageFormat}, or, if the message is not found, a supplied
	 * default value is returned.
	 * 
	 * @param key     the resource bundle key that is to be searched for
	 * @param message the default value which will be returned if no message is
	 *                found
	 * @param args    an array args to be used in a {@link java.text.MessageFormat}
	 *                message
	 * @return the message as found in the resource bundle, or defaultValue if none
	 *         is found
	 */
	public ValidationError(final String key, final String message, final String... args) {
		this.key = key;
		this.message = message;
		this.args = args;
	}

	public ValidationError(String key, String message, boolean nonFieldError, String... args) {
		this(key, message, args);
		this.nonFieldError = nonFieldError;
	}

	public String getKey() {
		return key;
	}

	public String getMessage() {
		return message;
	}

	public String[] getArgs() {
		return args;
	}

	public boolean isNonFieldError() {
		return nonFieldError;
	}

	public void setNonFieldError(boolean nonFieldError) {
		this.nonFieldError = nonFieldError;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (key == null ? 0 : key.hashCode());
		result = prime * result + (message == null ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ValidationError other = (ValidationError) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (message == null) {
			return other.message == null;
		} else
			return message.equals(other.message);
	}

	@Override
	public String toString() {
		return "Key=" + key + ",Message=" + message;
	}
}
