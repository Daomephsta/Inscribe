package io.github.daomephsta.inscribe.client.guide.xmlformat;

public class InscribeXmlParseException extends RuntimeException
{
	private static final long serialVersionUID = 2979560630003595871L;

	public InscribeXmlParseException(String message)
	{
		super(message);
	}

	public InscribeXmlParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InscribeXmlParseException(String message, Throwable cause, boolean enableSuppression,
		boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
