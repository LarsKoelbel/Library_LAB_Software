package Library.xml;

import javax.management.modelmbean.XMLParseException;

/**
 * Parser for an xml node struct
 * @author lkoeble 21487
 */
public class XMLParser {

    private String xml = null;
    private int index = 0;

    /**
     * Contractor for a new xml parser on a xml string
     * @param _xml The xml string
     */
    public XMLParser(String _xml)
    {
        this.xml = _xml;
        this.index = 0;
    }

    /**
     * Get the next char
     * @return the next char or terminator
     */
    private char next()
    {
        if (index >= xml.length()) return '\0';
        return xml.charAt(index++);
    }

    /**
     * Get the next char without iterating up
     * @return The next char
     */
    private char nextNext()
    {
        if (index >= xml.length()) return '\0';
        return xml.charAt(index);
    }

    /**
     * Parse the xml string
     * @return The xml node
     * @throws XMLParseException Is the string cant be pared as valid xml
     */
    public XMLNode parse() throws XMLParseException {
        // Jump to first tag
        while (next() != '<');
        return parseXML();
    }

    /**
     * Parse a xml node
     * @return The xml node
     * @throws XMLParseException If the node cant be parsed
     */
    private XMLNode parseXML() throws XMLParseException {
        XMLNode xmlNode = new XMLNode();
        StringBuilder buffer = new StringBuilder();
        // Get the nme of the node
        char c;
        boolean selfClosing = false;
        while ((c = next()) != '>') {
            if (c == '/' && nextNext() == '>')
            {
                selfClosing = true;
                break;
            }
            if (Character.isWhitespace(c)) {
                // Handle attributes here (later, maybe)
                char n;
                while ((n=next()) != '>'){
                    if (n == '/' && nextNext() == '>') selfClosing = true;
                }
                break;
            };
            buffer.append(c);
        };
        // Add name to node
        xmlNode.setqName(buffer.toString());
        buffer.setLength(0);
        if (selfClosing) return xmlNode;
        // Get the content or the children of the node
        while (true)
        {
            c = next();
            if (c == '<')
            {
                if (nextNext() == '/')
                {
                    // End of the node
                    xmlNode.setContent(buffer.toString());
                    buffer.setLength(0);
                    next();
                    while ((c = next()) != '>') {
                        buffer.append(c);
                        if (Character.isWhitespace(c)) throw new XMLParseException("Illegal use of whitespace character '"+ c +"'  in tag qName '"+ buffer +"' at index " + index);
                    };
                    if (!buffer.toString().equals(xmlNode.getqName()))
                    {
                        throw new XMLParseException("Closing tag '"+ buffer +"' does not match opening tag '"+ xmlNode.getqName() +"' at index " + index);
                    }
                    return xmlNode;
                }
                else if (nextNext() != '\0')
                {
                    XMLNode child = parseXML();
                    xmlNode.addChild(child);
                }
                else
                {
                    throw new XMLParseException("XML ended without a closing tag node '"+ xmlNode.getqName() +"'");
                }
            }
            else
            {
                buffer.append(c);
            }

        }
    }
}
