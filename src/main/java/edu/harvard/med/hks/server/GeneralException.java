package edu.harvard.med.hks.server;

@SuppressWarnings("serial")
public class GeneralException extends Exception {

	public GeneralException() {
		super();
	}

	public GeneralException(String message) {
		super(message);
	}

	public GeneralException(String message, Throwable cause) {
		super(message, cause);
	}
}
