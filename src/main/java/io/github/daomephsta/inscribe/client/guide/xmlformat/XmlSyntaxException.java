package io.github.daomephsta.inscribe.client.guide.xmlformat;

public class XmlSyntaxException extends RuntimeException
{
	private static final long serialVersionUID = 2979560630003595871L;

	public XmlSyntaxException(String message)
	{
		super(message);
	}
	
	public XmlSyntaxException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public XmlSyntaxException(String message, Throwable cause, boolean enableSuppression,
		boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
