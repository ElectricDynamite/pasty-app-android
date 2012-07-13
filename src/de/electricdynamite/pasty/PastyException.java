package de.electricdynamite.pasty;

public class PastyException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8439075085500583159L;
	
	public static final short ERROR_AUTHORIZATION_FAILED = 1;
	public static final short ERROR_ILLEGAL_RESPONSE = 2;

	public PastyException() {
	}

	public PastyException(String msg) {
	    super(msg);
	}
	
	public PastyException(short errorId, String msg) {
		
		super(msg);
	}
}
