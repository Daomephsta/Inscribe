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
	
	public static Identifier withNamespace(Identifier base, String domain)
	{
		return new Identifier(domain, base.getPath());
	}
	
	public static Identifier withPath(Identifier base, String path)
	{
		return new Identifier(base.getNamespace(), path);
	}
	
	public static Identifier prefixPath(Identifier base, String prefix)
	{
		return new Identifier(base.getNamespace(), prefix + base.getPath());
	}
	
	public static Identifier suffixPath(Identifier base, String suffix)
	{
		return new Identifier(base.getNamespace(), base.getPath() + suffix);
	}
	
	public static Identifier addToPath(Identifier base, String prefix, String suffix)
	{
		return new Identifier(base.getNamespace(), prefix + base.getPath() + suffix);
	}
	
	public static Identifier subPath(Identifier base, int start)
	{
		return subPath(base, start, -1);
	}
	
	public static Identifier endRelativeSubPath(Identifier base, int endRelativeIndex)
	{
		List<String> pathElements = PATH_SPLITTER.splitToList(base.getPath());
		return new Identifier(base.getNamespace(), 
			String.join(PATH_SEPARATOR, pathElements.subList(0, pathElements.size() - endRelativeIndex)));
	}
	
	public static Identifier subPath(Identifier base, int start, int end)
	{
		List<String> pathElements = PATH_SPLITTER.splitToList(base.getPath());
		if (end == -1)
			end = pathElements.size();
		return new Identifier(base.getNamespace(), String.join(PATH_SEPARATOR, pathElements.subList(start, end)));
	}
	
	public static Builder builder(Identifier base)
	{
		return new Builder(base);
	}
	
	public static class Builder
	{
		private String namespace;
		private List<String> pathSegments;

		private Builder(Identifier base)
		{
			this.pathSegments = Lists.newArrayList(PATH_SPLITTER.split(base.getPath()));
		}
		
		public Builder namespace(String namespace)
		{
			this.namespace = namespace;
			return this;
		}
		
		public Builder path(String path)
		{
			this.pathSegments = Lists.newArrayList(PATH_SPLITTER.split(path));
			return this;
		}
		
		public Builder prefixPath(String prefix)
		{
			this.pathSegments.addAll(0, PATH_SPLITTER.splitToList(prefix));
			return this;
		}
		
		public Builder suffixPath(String suffix)
		{
			this.pathSegments.addAll(PATH_SPLITTER.splitToList(suffix));
			return this;
		}
		
		public Builder addToPath(String prefix, String suffix)
		{
			prefixPath(prefix);
			suffixPath(suffix);
			return this;
		}
		
		public Builder subPath(int start)
		{
			subPath(start, -1);
			return this;
		}
		
		public Builder endRelativeSubPath(int endRelativeIndex)
		{
			this.pathSegments = pathSegments.subList(0, pathSegments.size() - endRelativeIndex);
			return this;
		}
		
		public Builder subPath(int start, int end)
		{
			if (end == -1)
				end = pathSegments.size();
			this.pathSegments = pathSegments.subList(start, end);
			return this;
		}
		
		public Identifier build()
		{
			return new Identifier(namespace, String.join(PATH_SEPARATOR, pathSegments));
		}
	}
}
