package io.github.daomephsta.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;

public class Unindenter
{
    private static final Splitter LINE_BREAK = Splitter.on('\n').omitEmptyStrings();
    private int tabIndentValue = 4;

    public int getTabIndentValue()
    {
        return tabIndentValue;
    }

    public Unindenter setTabIndentValue(int tabIndentValue)
    {
        this.tabIndentValue = tabIndentValue;
        return this;
    }

    public String unindent(String input)
    {
        List<String> lines = LINE_BREAK.splitToList(input);
        String[] trimmedLines = new String[lines.size()];
        int[] indents = new int[lines.size()];
        int smallestIndent = Integer.MAX_VALUE;
        for (int l = 0; l < lines.size(); l++)
        {
            String line = lines.get(l);
            indents[l] = 0;

            int c = 0;
            while (c < line.length())
            {
                char ch = line.charAt(c);
                if (ch == '\t')
                    indents[l] += tabIndentValue;
                else if (ch == ' ')
                    indents[l] += 1;
                else
                    break;
                c++;
            }
            trimmedLines[l] = line.substring(c);
            if (indents[l] < smallestIndent)
                smallestIndent = indents[l];
        }

        String[] finalLines = new String[lines.size()];
        for (int l = 0; l < finalLines.length; l++)
        {
            int indent = indents[l];
            if (indent >= smallestIndent)
                indent -= smallestIndent;
            int finalLineLength = indent + trimmedLines[l].length();
            finalLines[l] = StringUtils.leftPad(trimmedLines[l], finalLineLength, ' ');
        }
        return String.join("\n", finalLines);
    }
}