package io.github.daomephsta.mosaic;

public abstract class MosaicWidget
{
    private int x, y, width, height;
    private EdgeSpacing padding = new EdgeSpacing(),
                        margin = new EdgeSpacing();

    @Override
    public String toString()
    {
        return String.format("%s [x=%s, y=%s, width=%s, height=%s]",
                this.getClass().getSimpleName(), x, y, width, height);
    }

    public int x()
    {
        return x;
    }

    public int y()
    {
        return y;
    }

    public int width()
    {
        return width;
    }

    public int height()
    {
        return height;
    }

    public void setLayoutParameters(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int hintHeight() 
    { 
        return 1; 
    }

    public int hintWidth() 
    { 
        return 1; 
    }

    public EdgeSpacing padding()
    {
        return padding;
    }

    public void setPadding(EdgeSpacing padding)
    {
        this.padding = padding;
    }

    public EdgeSpacing margin()
    {
        return margin;
    }

    public void setMargin(EdgeSpacing margin)
    {
        this.margin = margin;
    }
}
