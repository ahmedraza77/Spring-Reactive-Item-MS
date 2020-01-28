package com.learnreactive.fluxandmono;

public class CustomException extends Throwable {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 5077931519385557982L;
	private String message;

	    @Override
	    public String getMessage() {
	        return message;
	    }

	    public void setMessage(String message) {
	        this.message = message;
	    }

	    public CustomException(Throwable e) {
	        this.message = e.getMessage();
	    }
	    
}
