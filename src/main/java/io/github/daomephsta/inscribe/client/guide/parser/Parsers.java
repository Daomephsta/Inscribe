package io.github.daomephsta.inscribe.client.guide.parser;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.ProcessingInstruction;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;

import io.github.daomephsta.inscribe.client.guide.GuideIdentifier;
import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.v100.V100Parser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlNodes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.common.util.Identifiers;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class Parsers
{
    private static final Map<ParserVersion, Parser> PARSERS = ImmutableMap.<ParserVersion, Parser>builder()
            .put(new ParserVersion(1, 0, 0), V100Parser.INSTANCE)
            .build();
    private static final ThreadLocal<String> LAST_VERSION = ThreadLocal.withInitial(() -> "");
    private static final ThreadLocal<Parser> LAST_PARSER = new ThreadLocal<>();

    public static GuideDefinition loadGuideDefinition(Document doc, ResourceManager resourceManager, GuideIdentifier filePath) throws GuideLoadingException
    {
        return getParser(doc).loadGuideDefinition(doc, resourceManager, filePath);
    }

    public static XmlEntry loadEntry(Document doc, ResourceManager resourceManager, GuideIdentifier filePath) throws GuideLoadingException
    {
        return getParser(doc).loadEntry(doc, resourceManager, deriveId(filePath), filePath);
    }

    public static TableOfContents loadTableOfContents(Document doc, ResourceManager resourceManager, GuideIdentifier filePath) throws GuideLoadingException
    {
        return getParser(doc).loadTableOfContents(doc, deriveId(filePath), filePath);
    }

    private static Identifier deriveId(GuideIdentifier filePath) throws GuideLoadingException
    {
        return Identifiers.working(filePath.getGuideId())
            .addPathSegment(filePath.getSectionPath())
            .editPathSegment(-1, FilenameUtils::removeExtension)
            .toIdentifier();
    }

    private static Parser getParser(Document doc) throws InscribeSyntaxException
    {
        ProcessingInstruction version = XmlNodes.getProcessingInstruction(doc, "parser_version");
        if (version == null)
            throw new InscribeSyntaxException(doc + " missing 'parser_version' processing instruction");
        if (LAST_VERSION.get().equals(version.getData()))
            return LAST_PARSER.get();
        try
        {
            Parser parser = PARSERS.get(ParserVersion.parse(version.getData()));
            if (parser == null)
                throw invalidVersionException(version.getData());
            LAST_VERSION.set(version.getData());
            LAST_PARSER.set(parser);
            return parser;
        }
        catch (ParserVersion.InvalidVersionException e)
        {
            throw invalidVersionException(e.versionString);
        }
    }

    private static InscribeSyntaxException invalidVersionException(String versionString)
    {
        return new InscribeSyntaxException(String.format(
                "Invalid parser version '%s'. Valid versions: %s", versionString,
                PARSERS.keySet().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "))));
    }

    private static class ParserVersion
    {
        private static final Pattern VERSION_PATTERN = Pattern.compile("(?<major>\\d+).(?<minor>\\d+).(?<patch>\\d+)");
        private final int major,
                          minor,
                          patch;
        private final String versionString;

        public ParserVersion(int major, int minor, int patch)
        {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.versionString = major + "." + minor + "." + patch;
        }

        public static ParserVersion parse(String versionString) throws InvalidVersionException
        {
            Matcher versionMatcher = VERSION_PATTERN.matcher(versionString);
            if (!versionMatcher.matches())
                throw new InvalidVersionException(versionString);
            int major = Ints.tryParse(versionMatcher.group("major")),
                minor = Ints.tryParse(versionMatcher.group("minor")),
                patch = Ints.tryParse(versionMatcher.group("patch"));
            return new ParserVersion(major, minor, patch);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(major, minor, patch);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ParserVersion other = (ParserVersion) obj;
            return major == other.major
                && minor == other.minor
                && patch == other.patch;
        }

        @Override
        public String toString()
        {
            return versionString;
        }

        private static class InvalidVersionException extends Exception
        {
            private final String versionString;

            private InvalidVersionException(String versionString)
            {
                this.versionString = versionString;
            }
        }
    }
}
