package io.github.daomephsta.mosaic;

public class EdgeSpacing
{
    private int left = 0,
                right = 0,
                top = 0,
                bottom = 0;

    public EdgeSpacing setLeft(int left)
    {
        this.left = left;
        return this;
    }

    public int left()
    {
        return this.left;
    }

    public EdgeSpacing setRight(int right)
    {
        this.right = right;
        return this;
    }

    public int right()
    {
        return this.right;
    }

    public EdgeSpacing setTop(int top)
    {
        this.top = top;
        return this;
    }

    public int top()
    {
        return this.top;
    }

    public EdgeSpacing setBottom(int bottom)
    {
        this.bottom = bottom;
        return this;
    }

    public int bottom()
    {
        return this.bottom;
    }

    public EdgeSpacing setHorizontal(int value)
    {
        this.left = this.right = value;
        return this;
    }

    public EdgeSpacing setVertical(int value)
    {
        this.top = this.bottom = value;
        return this;
    }

    public EdgeSpacing setAll(int value)
    {
        this.left = this.right = this.top = this.bottom = value;
        return this;
    }
}
