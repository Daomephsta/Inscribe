package io.github.daomephsta.inscribe.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import net.minecraft.util.Identifier;

public class Identifiers
{
    public static final String PATH_SEPARATOR = "/";
    public static final char PATH_SEPARATOR_CHAR = '/';
    private static final Splitter PATH_SPLITTER = Splitter.on(PATH_SEPARATOR_CHAR),
                                  IDENTIFIER_SPLITTER = Splitter.on(CharMatcher.anyOf(":" + PATH_SEPARATOR));


    public static Working working(String base)
    {
        Iterator<String> split = IDENTIFIER_SPLITTER.split(base).iterator();
        String namespace = split.next();
        ArrayList<String> path = Streams.stream(split).collect(Collectors.toCollection(ArrayList::new));
        return new Working(namespace, path);
    }

    public static Working working(Identifier base)
    {
        return new Working(base);
    }

    public static class Working
    {
        private String namespace;
        private List<String> pathSegments;

        private Working(String namespace, List<String> pathSegments)
        {
            this.namespace = namespace;
            this.pathSegments = pathSegments;
        }

        private Working(Identifier base)
        {
            this.namespace(base.getNamespace()).path(base.getPath());
        }

        public Working namespace(String namespace)
        {
            this.namespace = namespace;
            return this;
        }

        public Working editNamespace(UnaryOperator<String> namespace)
        {
            this.namespace = namespace.apply(this.namespace);
            return this;
        }

        public Working editPathSegment(int index, UnaryOperator<String> segment)
        {
            if (index < 0)
                index += pathSegments.size();
            pathSegments.set(index, segment.apply(pathSegments.get(index)));
            return this;
        }

        public String getSegment(int index)
        {
            return pathSegments.get(index);
        }

        public int indexOf(String pathSegment)
        {
            return pathSegments.indexOf(pathSegment);
        }

        public Working path(String path)
        {
            this.pathSegments = splitPathMutable(path);
            return this;
        }

        private static ArrayList<String> splitPathMutable(String path)
        {
            return Lists.newArrayList(PATH_SPLITTER.split(path));
        }

        public Working subIdentifier(int from, int to)
        {
            if (from < 0)
                from += pathSegments.size();
            if (to < 0) // Negative tos are inclusive
                to = to + pathSegments.size() + 1;
            return new Working(namespace, pathSegments.subList(from, to));
        }

        public Working addPathSegment(String segment)
        {
            pathSegments.add(segment);
            return this;
        }

        public Working addPathSegment(int index, String segment)
        {
            if (index < 0)
                index += pathSegments.size();
            pathSegments.add(index, segment);
            return this;
        }

        public Working addPathSegments(Collection<? extends String> segments)
        {
            pathSegments.addAll(segments);
            return this;
        }

        public Working addPathSegments(int index, Collection<? extends String> segments)
        {
            if (index < 0)
                index += pathSegments.size();
            pathSegments.addAll(index, segments);
            return this;
        }

        public Working addPathSegments(String... segments)
        {
            Collections.addAll(this.pathSegments, segments);
            return this;
        }

        public Working addPathSegments(int index, String... segments)
        {
            if (index < 0)
                index += pathSegments.size();
            Collections.addAll(this.pathSegments.subList(0, index + 1), segments);
            return this;
        }

        public Working clearPath()
        {
            pathSegments.clear();
            return this;
        }

        public Identifier toIdentifier()
        {
            return new Identifier(namespace, toPath());
        }

        public String toPath()
        {
            return String.join(PATH_SEPARATOR, pathSegments);
        }
    }
}
