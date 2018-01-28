package org.springframework.data.chaincode.sdk.client;

public class InstantiateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InstantiateException() {
		super();
	}

	public InstantiateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InstantiateException(String message, Throwable cause) {
		super(message, cause);
	}

	public InstantiateException(String message) {
		super(message);
	}

	public InstantiateException(Throwable cause) {
		super(cause);
	}

}
