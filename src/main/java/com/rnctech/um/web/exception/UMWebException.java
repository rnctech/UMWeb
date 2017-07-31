package com.rnctech.um.web.exception;

/**
 * @contributor zilin
 * 
 */

public class UMWebException extends Exception {

	private static final long serialVersionUID = 5199681935290826055L;

	public UMWebException() {
		super("Current tenant not authenticated with its MR system.");
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 * @param msgArgs
	 */
	public UMWebException(String message, Throwable cause, Object... msgArgs) {
		super(message, cause);
	}

	/**
	 * 
	 * @param message
	 * @param msgArgs
	 */
	public UMWebException(String message, Object... msgArgs) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public UMWebException(Throwable cause) {
		super(cause);
	}


}
