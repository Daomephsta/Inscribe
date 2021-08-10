package io.github.daomephsta.inscribe.client.guide.template;

import static java.util.stream.Collectors.toMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.logging.log4j.util.Strings;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XPaths;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlNodes;

public class Template
{
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{(\\w+)\\}");
    private final Node templateElement;
    private final TemplateProcessor processor;
    
    public Template(Node templateElement, TemplateProcessor processor)
    {
        this.templateElement = templateElement;
        this.processor = processor;
    }
    
    public void replaceUsage(Element arguments)
    {
        NamedNodeMap attributes = arguments.getAttributes();
        Arguments args = new Arguments(IntStream.range(0, attributes.getLength())
            .mapToObj(i -> (Attr) attributes.item(i))
            .collect(toMap(Attr::getNodeName, Attr::getValue)));
        processor.process(args);

        Node substituted = templateElement.cloneNode(true);
        // Iterate all text nodes
        for (Node child : XPaths.nodes(substituted, "//text()"))
        {
            if (child instanceof Text text && !Strings.isBlank(text.getData()))
                text.setData(replacePlaceholders(args, text.getData()));
        }
        // Iterate all attributes not of <template>
        for (Node child : XPaths.nodes(substituted, "descendant::*/@*"))
        {
            Attr attr = (Attr) child;
            attr.setValue(replacePlaceholders(args, attr.getValue()));
        }
        // Replace <contents> with contents of arguments element
        for (Node node : XPaths.nodes(substituted, "//contents"))
        {
            XmlNodes.replaceChild(node, XPaths.nodes(arguments, "./node()"));
        }
        // Replace arguments element with substituted template
        XmlNodes.replaceChild(arguments, XPaths.nodes(substituted, "./node()"));
    }

    private String replacePlaceholders(Arguments args, String input)
    {
        var replaced = new StringBuilder(input);
        Matcher matcher = PLACEHOLDER.matcher(input);
        while (matcher.find())
        {
            String placeholder = matcher.group(1);
            replaced.replace(matcher.start(), matcher.end(), args.getString(placeholder));
        }
        return replaced.toString();
    }
}
