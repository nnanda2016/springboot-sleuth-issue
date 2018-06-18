package com.demo.springboot.sleuth.exception;

/**
 * <p>
 * A runtime exception which can be used or extended by applications
 * for exception management.
 *
 * @author Niranjan Nanda
 */
public class AppException extends RuntimeException {
	// Serial version UID.
	private static final long serialVersionUID = 1L;

	private final String errorCode;
	private final String errorMessage;

	public AppException(
			final String errorCode,
			final String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public AppException(
			final String errorCode,
			final String errorMessage,
			final Throwable cause) {
		super(errorMessage, cause);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Returns the value of errorMessage.
	 *
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Getter for {@code errorCode}
	 *
	 * @return Error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("AppException: { ");
		builder.append(errorCode);
		builder.append(": ");
		builder.append(errorMessage);

		if (getCause() != null) {
			builder.append(" [cause=");
			builder.append(getCause().toString());
		}

		builder.append("]}");
		return builder.toString();
	}
}
