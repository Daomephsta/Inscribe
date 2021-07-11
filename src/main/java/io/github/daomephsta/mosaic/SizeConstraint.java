package io.github.daomephsta.mosaic;

public abstract class SizeConstraint
{
    public static final SizeConstraint DEFAULT = new SizeConstraint()
    {
        @Override
        public double toAbsolute(double max)
        {
            return 0; //Guarantees that minimum size will kick in
        }

        @Override
        public String toString()
        {
            return "DEFAULT";
        }
    };

    public static SizeConstraint pixels(double pixels)
    {
        return UnitSizeConstraint.pixels(pixels);
    }

    public static SizeConstraint percentage(double percentage)
    {
        return UnitSizeConstraint.percentage(percentage);
    }

    public static SizeConstraint parse(String s) throws ParseException
    {
        if (s.equals("default"))
            return DEFAULT;
        SizeConstraint size = UnitSizeConstraint.parse(s);
        if (size == null)
            throw new ParseException("Could not parse " + s + " as a size constraint.");
        return size;
    }

    public abstract double toAbsolute(double max);
}
