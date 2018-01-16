package org.springframework.data.chaincode.repository.support;

public class ChaincodeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ChaincodeException() {
		super();
	}

	public ChaincodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChaincodeException(String message) {
		super(message);
	}

	public ChaincodeException(Throwable cause) {
		super(cause);
	}
	
	

}
