package com.mcbans.rona_tombo.mcbansbungee.exception;

class MCBansException extends Exception{
	private static final long serialVersionUID = - 1420944571331163458L;

	MCBansException(final String message){
		super(message);
	}

	MCBansException(final Throwable cause){
		super(cause);
	}

	MCBansException(final String message, final Throwable cause){
		super(message, cause);
	}
}
