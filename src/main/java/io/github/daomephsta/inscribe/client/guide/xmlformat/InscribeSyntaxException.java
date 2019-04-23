package io.github.daomephsta.inscribe.client.guide.xmlformat;

public class InscribeSyntaxException extends RuntimeException
{
	private static final long serialVersionUID = 2979560630003595871L;

	public InscribeSyntaxException(String message)
	{
		super(message);
	}
	
	public InscribeSyntaxException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InscribeSyntaxException(String message, Throwable cause, boolean enableSuppression,
		boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
