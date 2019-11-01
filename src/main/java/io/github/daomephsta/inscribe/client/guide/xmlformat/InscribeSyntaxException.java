package io.github.daomephsta.inscribe.client.guide.xmlformat;

/**
 * Thrown when XML is valid as XML, but invalid as the Inscribe XML DSL.
 * Non-fatal. Should always be caught and the message logged.
 *
 * @author Daomephsta
 */
public class InscribeSyntaxException extends Exception
{
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
