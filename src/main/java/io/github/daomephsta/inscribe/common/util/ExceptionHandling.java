package io.github.daomephsta.inscribe.common.util;

import com.pivovarit.function.exception.WrappedException;

public class ExceptionHandling
{
    /**
     * 'Unwraps' a throwable, discarding exceptions that wrap rather than add information
     * @param thrw the throwable to unwrap
     * @return the first throwable in the chain that doesn't only wrap
     */
    public static Throwable unwrap(Throwable thrw)
    {
        while(thrw instanceof WrappedException || 
            (thrw.getCause() != null && thrw.getCause().toString().equals(thrw.getMessage())))
        {
            thrw = thrw.getCause();
        }
        return thrw;
    }
}
