package io.github.daomephsta.mosaic.flow;

import io.github.daomephsta.mosaic.Size;
import io.github.daomephsta.mosaic.SizeConstraint;

public class FlowLayoutData
{
    private final Size size;

    public FlowLayoutData(Size size)
    {
        this.size = size;
    }

    public FlowLayoutData()
    {
        this(new Size());
    }

    public int computeSize(double parentSize)
    {
        return size.computeSize(parentSize);
    }

    public FlowLayoutData setPreferredSize(SizeConstraint preferred)
    {
        size.setPreferredSize(preferred);
        return this;
    }

    public FlowLayoutData setDefaultPreferredSize(SizeConstraint preferred)
    {
        size.setDefaultPreferredSize(preferred);
        return this;
    }

    public FlowLayoutData setMinSize(int minSize)
    {
        size.setMinSize(minSize);
        return this;
    }

    public FlowLayoutData setDefaultMinSize(int minSize)
    {
        size.setDefaultMinSize(minSize);
        return this;
    }

    public FlowLayoutData setMaxSize(int maxSize)
    {
        size.setMaxSize(maxSize);
        return this;
    }

    public FlowLayoutData setDefaultMaxSize(int maxSize)
    {
        size.setDefaultMaxSize(maxSize);
        return this;
    }

    @Override
    public String toString()
    {
        return String.format("FlowLayoutData(size: %s)", size);
    }
}
