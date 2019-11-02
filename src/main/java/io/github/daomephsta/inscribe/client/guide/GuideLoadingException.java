package io.github.daomephsta.inscribe.client.guide;

/**
 * Thrown when an error occurs during GuideLoading.
 * If {@code severity == Severity.NON_FATAL} it will be caught and logged by the guide manager, then loading will continue.
 * If {@code severity == Severity.FATAL} it will be caught and logged by the guide manager, then loading will halt.
 * @author Daomephsta
 */
public class GuideLoadingException extends Exception
{
	public enum Severity
	{
		NON_FATAL,
		FATAL;
	}

	private final Severity severity;

	public GuideLoadingException(String message, Severity severity)
	{
		super(message);
		this.severity = severity;
	}

	public GuideLoadingException(Throwable cause, Severity severity)
	{
		super(cause);
		this.severity = severity;
	}

	public GuideLoadingException(String message, Throwable cause, Severity severity)
	{
		super(message, cause);
		this.severity = severity;
	}

	public boolean isFatal()
	{
		return severity == Severity.FATAL;
	}
}
