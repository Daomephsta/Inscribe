package io.github.daomephsta.mosaic;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class UnitSizeConstraint extends SizeConstraint
{
    private static final Pattern PATTERN = Pattern
            .compile("(?<magnitude>\\d+(?:\\.\\d+)?)(?<unit>.+)");

    private final double magnitude;
    private final Unit unit;

    private UnitSizeConstraint(double magnitude, Unit unit)
    {
        unit.validate(magnitude);
        this.magnitude = magnitude;
        this.unit = unit;
    }

    public static UnitSizeConstraint pixels(double pixels)
    {
        return new UnitSizeConstraint(pixels, Unit.PIXELS);
    }

    public static UnitSizeConstraint percentage(double percentage)
    {
        return new UnitSizeConstraint(percentage, Unit.PERCENTAGE);
    }

    public static UnitSizeConstraint parse(String s) throws ParseException
    {
        Matcher matcher = PATTERN.matcher(s);
        if (matcher.matches())
        {
            try
            {
                double magnitude = Double
                        .parseDouble(matcher.group("magnitude"));
                String unitString = matcher.group("unit");
                Unit unit = Unit.UNIT_BY_ABBREVIATION.get(unitString);
                if (unit == null)
                    throw new ParseException("Unknown unit " + unitString);
                return new UnitSizeConstraint(magnitude, unit);
            }
            catch (IllegalArgumentException nfe)
            {
                throw new ParseException(nfe.getMessage());
            }
        }
        return null;
    }

    @Override
    public double toAbsolute(double max)
    {
        return unit.toAbsolute(magnitude, max);
    }

    @Override
    public String toString()
    {
        return String.format("UnitSizeConstraint(magnitude: %s, unit: %s)", magnitude, unit);
    }

    private enum Unit
    {
        PIXELS("px")
        {
            @Override
            public double toAbsolute(double magnitude, double max)
            {
                return magnitude;
            }
        },
        PERCENTAGE("%")
        {
            @Override
            public double toAbsolute(double magnitude, double max)
            {
                return magnitude / 100.0D * max;
            }

            @Override
            void validate(double magnitude)
            {
                if (magnitude > 100)
                    throw new IllegalArgumentException(
                            "Magnitude is greater than 100%");
            }
        };

        private static final Map<String, Unit> UNIT_BY_ABBREVIATION;
        static
        {
            UNIT_BY_ABBREVIATION = Arrays.stream(values())
                    .collect(Collectors.toMap(u -> u.abbreviation, u -> u));
        }

        private final String abbreviation;

        private Unit(String abbreviation)
        {
            this.abbreviation = abbreviation;
        }

        public abstract double toAbsolute(double magnitude, double max);
        void validate(double magnitude)
        {
        }
    }
}
