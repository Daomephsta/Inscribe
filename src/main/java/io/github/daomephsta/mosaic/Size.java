package io.github.daomephsta.mosaic;

public class Size
{
    private static final SizeConstraint DEFAULT_PREFERRED_SIZE = SizeConstraint.DEFAULT;
    private static final int DEFAULT_MAX_SIZE = Integer.MAX_VALUE;
    private static final int DEFAULT_MIN_SIZE = Integer.MIN_VALUE;
    private SizeConstraint preferredSize;
    private int minSize,
                maxSize;

    public Size()
    {
        this.preferredSize = DEFAULT_PREFERRED_SIZE;
        this.minSize = DEFAULT_MIN_SIZE;
        this.maxSize = DEFAULT_MAX_SIZE;
    }

    public int computeSize(double parentSize)
    {
        // Clamp within [minSize, maxSize] and round
        return (int) Math.ceil(Math.min(Math.max(preferredSize.toAbsolute(parentSize), minSize), maxSize));
    }

    public void setPreferredSize(SizeConstraint preferred)
    {
        this.preferredSize = preferred;
    }

    public void setDefaultPreferredSize(SizeConstraint preferred)
    {
        if (this.preferredSize == DEFAULT_PREFERRED_SIZE)
            setPreferredSize(preferred);
    }

    public void setMinSize(int minSize)
    {
        this.minSize = minSize;
    }

    public void setDefaultMinSize(int minSize)
    {
        if (this.minSize == DEFAULT_MIN_SIZE)
            setMinSize(minSize);
    }

    public void setMaxSize(int maxSize)
    {
        this.maxSize = maxSize;
    }

    public void setDefaultMaxSize(int maxSize)
    {
        if (this.maxSize == DEFAULT_MAX_SIZE)
            setMaxSize(maxSize);
    }

    public boolean isDefault()
    {
        return preferredSize == DEFAULT_PREFERRED_SIZE && minSize == DEFAULT_MIN_SIZE && maxSize == DEFAULT_MAX_SIZE;
    }

    @Override
    public String toString()
    {
        return String.format("Size(preferredSize: %s, minSize: %s, maxSize: %s)", preferredSize, minSize, maxSize);
    }
}
