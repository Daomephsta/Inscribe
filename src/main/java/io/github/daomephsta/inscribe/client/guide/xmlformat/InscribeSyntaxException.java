package io.github.daomephsta.inscribe.client.guide.xmlformat;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;

/**
 * Thrown when XML is valid as XML, but invalid as the Inscribe XML DSL.
 * Non-fatal. Should always be caught and the message logged.
 *
 * @author Daomephsta
 */
public class InscribeSyntaxException extends GuideLoadingException
{
	public InscribeSyntaxException(String message)
	{
		super(message, Severity.NON_FATAL);
	}

	public InscribeSyntaxException(String message, Throwable cause)
	{
		super(message, cause, Severity.NON_FATAL);
	}
}
