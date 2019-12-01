package io.github.daomephsta.util;

import java.util.*;

import com.google.common.base.Splitter;
import com.google.common.collect.*;

import net.minecraft.util.Identifier;

public class Identifiers
{
    public static final String PATH_SEPARATOR = "/";
    public static final char PATH_SEPARATOR_CHAR = '/';
    private static final Splitter PATH_SPLITTER = Splitter.on(PATH_SEPARATOR_CHAR);

    public static Identifier withNamespace(Identifier base, String namespace)
    {
        return builder(base).namespace(namespace).build();
    }

    public static Identifier withPath(Identifier base, String path)
    {
        return builder(base).path(path).build();
    }

    public static Identifier prefixPath(Identifier base, String prefix)
    {
        return builder(base).prependPathSegments(prefix).build();
    }

    public static Identifier suffixPath(Identifier base, String suffix)
    {
        return builder(base).appendPathSegments(suffix).build();
    }

    public static Identifier subPath(Identifier base, int start)
    {
        return builder(base).subPath(start).build();
    }

    public static Identifier subPath(Identifier base, int start, int end)
    {
        return builder(base).subPath(start, end).build();
    }

    public static Identifier replace(Identifier base, int start, String replacement)
    {
        List<String> pathSegments = splitPathMutable(base.getPath());
        checkStartBounds(start, pathSegments.size());
        List<String> replacementSegments = splitPath(replacement);
        pathSegments.remove(start);
        pathSegments.addAll(start, replacementSegments);
        return createIdentifier(base.getNamespace(), pathSegments);
    }

    public static Identifier replaceFromEnd(Identifier base, int start, String replacement)
    {
        return builder(base).replaceFromEnd(start, replacement).build();
    }

    public static Builder builder(Identifier base)
    {
        return new Builder(base);
    }

    public static Builder builder(String namespace)
    {
        return new Builder(namespace);
    }

    public static class Builder
    {
        private String namespace;
        private List<String> pathSegments;

        private Builder(String namespace)
        {
            namespace(namespace);
            this.pathSegments = new ArrayList<>();
        }

        private Builder(Identifier base)
        {
            this.namespace(base.getNamespace()).path(base.getPath());
        }

        public Builder namespace(String namespace)
        {
            this.namespace = namespace;
            return this;
        }

        public Builder path(String path)
        {
            this.pathSegments = splitPathMutable(path);
            return this;
        }

        public Builder prependPathSegments(String[] prefixes)
        {
            for (String prefix : prefixes)
                this.pathSegments.addAll(0, splitPath(prefix));
            return this;
        }

        public Builder prependPathSegments(String prefix)
        {
            this.pathSegments.addAll(0, splitPath(prefix));
            return this;
        }

        public Builder appendPathSegments(String firstSuffix, String... suffixes)
        {
            appendPathSegments(firstSuffix);
            for (String suffix : suffixes)
                appendPathSegments(suffix);
            return this;
        }

        public Builder appendPathSegments(String[] suffixes)
        {
            for (String suffix : suffixes)
                appendPathSegments(suffix);
            return this;
        }

        public Builder appendPathSegments(String suffix)
        {
            this.pathSegments.addAll(splitPath(suffix));
            return this;
        }

        public Builder prefixPath(String prefix)
        {
            this.pathSegments.set(0, prefix + this.pathSegments.get(0));
            return this;
        }

        public Builder suffixPath(String suffix)
        {
            int last = this.pathSegments.size() - 1;
            this.pathSegments.set(last, this.pathSegments.get(last) + suffix);
            return this;
        }

        public Builder subPath(int start)
        {
            checkStartBounds(start, pathSegments.size());
            subPath(start, -1);
            return this;
        }

        public Builder subPath(int start, int end)
        {
            checkStartBounds(start, pathSegments.size());
            if (end < 0)
                end = pathSegments.size() + 1 + end;
            checkEndBounds(end, pathSegments.size());
            this.pathSegments = pathSegments.subList(start, end);
            return this;
        }

        public Builder replace(int start, String replacement)
        {
            checkStartBounds(start, pathSegments.size());
            List<String> replacementSegments = splitPath(replacement);
            pathSegments.remove(start);
            pathSegments.addAll(start, replacementSegments);
            return this;
        }

        public Builder replaceFromEnd(int start, String replacement)
        {
            checkStartBounds(start, pathSegments.size());
            List<String> replacementSegments = splitPath(replacement);
            int absoluteStart = pathSegments.size() - 1 - start;
            pathSegments.remove(absoluteStart);
            pathSegments.addAll(absoluteStart, replacementSegments);
            return this;
        }

        public Identifier build()
        {
            return createIdentifier(namespace, pathSegments);
        }
    }

    private static List<String> splitPath(String path)
    {
        return PATH_SPLITTER.splitToList(path);
    }

    private static ArrayList<String> splitPathMutable(String path)
    {
        return Lists.newArrayList(PATH_SPLITTER.split(path));
    }

    private static Identifier createIdentifier(String namespace, List<String> pathSegments)
    {
        return new Identifier(namespace, String.join(PATH_SEPARATOR, pathSegments));
    }

    private static void checkStartBounds(int index, int segmentCount)
    {
        if (index < 0 || index >= segmentCount)
            throw new IllegalArgumentException(String.format("Start index value %d is outside of the range [0, %d)",
                index, segmentCount));
    }

    private static void checkEndBounds(int index, int segmentCount)
    {
        if (index < 0 || index > segmentCount)
            throw new IllegalArgumentException(String.format("End index value %d is outside of the range [0, %d]",
                index, segmentCount));
    }
}
