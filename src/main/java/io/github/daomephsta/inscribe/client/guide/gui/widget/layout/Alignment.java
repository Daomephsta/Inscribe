package io.github.daomephsta.inscribe.client.guide.gui.widget.layout;

import io.github.daomephsta.mosaic.MosaicWidget;

public enum Alignment
{
    LEADING
    {
        @Override
        protected float offset(int parentSize, int elementSize)
        {
            return 0;
        }
    },
    CENTER
    {
        @Override
        protected float offset(int parentSize, int elementSize)
        {
            return (parentSize - elementSize) / 2.0F;
        }
    },
    TRAILING
    {
        @Override
        protected float offset(int parentSize, int elementSize)
        {
            return parentSize - elementSize;
        }
    };

    public float offsetX(int x, MosaicWidget elementParent, int elementWidth)
    {
        return x + elementParent.padding().left() +
            offset(elementParent.width() - elementParent.padding().left() - elementParent.padding().right(), elementWidth);
    }

    public float offsetY(int y, MosaicWidget elementParent, int elementHeight)
    {
        return y + elementParent.padding().top() +
            offset(elementParent.height() - elementParent.padding().top() - elementParent.padding().bottom(), elementHeight);
    }

    protected abstract float offset(int parentSize, int elementSize);
}
