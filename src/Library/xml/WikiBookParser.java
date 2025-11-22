package Library.xml;

import Library.Medium.ElectronicalMedium;
import Library.Medium.ElectronicalMediumWikiBooks;
import Library.Medium.Medium;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.utils.URLUtils;

import javax.management.modelmbean.XMLParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

/**
* Class used to load and parse an element from WikiBooks
* @author lkoeble 21487
*/
public class WikiBookParser {

    /**
     * Get the name of the last contributor to a WikiBooks page
     * @param _bookName Name of the book to fetch the contributor of
     * @param _out Process output buffer
     * @return The name os null in case of errors
     */
    public String getContributorName(String _bookName, ProcessOutputBuffer _out)
    {
        String xml = fetchBook(_bookName, _out);
        return getContributorName(_bookName, _out, xml);
    }

    public String getContributorName(String _bookName, ProcessOutputBuffer _out, String xml)
    {
        if (xml == null) return null;
        try
        {
            // Parse the xml
            XMLParser parser = new XMLParser(xml);
            XMLNode root = parser.parse();
            // Get the path
            XMLNode contributors = root.get("page").get("revision").get("contributor");
            if (contributors.isNull())
            {
                _out.write("XML response from server does not contain contributor information", Severity.WARNING);
                return null;
            }
            // Get the contributor name
            if (contributors.has("username"))
            {
                return contributors.get("username").getContent();
            }
            if (contributors.has("ip"))
            {
                return contributors.get("ip").getContent();
            }
            _out.write("Failed to find username or ip of the last contributor", Severity.WARNING);
            return null;
        }catch (XMLParseException e)
        {
            _out.write("Failed to parse xml response: " + e.getMessage(), Severity.ERROR);
            return null;
        }
    }

    /**
     * Get the timestamp of the last revision
     * @param _bookName Name of the book to fetch the timestamp of
     * @param _out Process output buffer
     * @return The timestamp os null in case of errors
     */
    public LocalDateTime getRevisionTimestamp(String _bookName, ProcessOutputBuffer _out)
    {
        String xml = fetchBook(_bookName, _out);
        return getRevisionTimestamp(_bookName,_out,xml);
    }

    public LocalDateTime getRevisionTimestamp(String _bookName, ProcessOutputBuffer _out, String xml)
    {
        if (xml == null) return null;
        try
        {
            // Parse the xml
            XMLParser parser = new XMLParser(xml);
            XMLNode root = parser.parse();
            // Get the path
            XMLNode timestamp = root.get("page").get("revision").get("timestamp");
            if (timestamp.isNull())
            {
                _out.write("XML response from server does not contain timestamp information", Severity.WARNING);
                return null;
            }
            // Get the timestamp
            LocalDateTime local = Instant.parse(timestamp.getContent())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            return local;
        }catch (XMLParseException | DateTimeParseException e)
        {
            _out.write("Failed to parse xml response: " + e.getMessage(), Severity.ERROR);
            return null;
        }
    }

    /**
     * Get the table of contents from a given xml struct
     * @param _out Process output buffer
     * @param _xml The xml struct (root)
     * @return The table of contents or null in case of errors
     */
    private String getTableOfContents(ProcessOutputBuffer _out, XMLNode _xml)
    {
        StringBuilder sb = new StringBuilder();
        XMLNode text = _xml.get("page").get("revision").get("text");
        if (text == null)
        {
            _out.write("Failed to find table of contents in xml", Severity.WARNING);
            return null;
        }
        String[] lines = text.getContent().split("\n");
        for (String line : lines)
        {
            if (line.contains("===")) sb.append("\t").append(line.replace("===", "").strip()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Get the shelf from a given xml struct
     * @param _out Process output buffer
     * @param _xml The xml struct (root)
     * @return The shelf or null in case of errors
     */
    private String getShelf(ProcessOutputBuffer _out, XMLNode _xml)
    {
        StringBuilder sb = new StringBuilder();
        XMLNode text = _xml.get("page").get("revision").get("text");
        if (text == null)
        {
            _out.write("Failed to find shelf in xml", Severity.WARNING);
            return null;
        }
        String[] lines = text.getContent().split("\n");
        for (String line : lines)
        {
            if (line.contains("Regal")) return line.replace("{","").replace("}","").strip();
        }
        return "N/A";
    }

    /**
     * Fetch the xml to a book
     * @param _bookName Name of the book
     * @param _out Process output buffer
     * @return The string or null in case of errors
     */
    private String fetchBook(String _bookName, ProcessOutputBuffer _out)
    {
        String url = "https://de.wikibooks.org/wiki/Spezial:Exportieren/" + _bookName.strip().replace(" ","_");
        try {
            URLConnection conn = new URL(url).openConnection();

            // IMPORTANT: Wikimedia blocks requests without this
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Java XML Fetcher)");

            try (InputStream in = conn.getInputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
            {
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                }

                return sb.toString();
            }
        } catch (IOException e) {
            _out.write("Failed to load xml from url: " + url + " :: " + e.getMessage(), Severity.ERROR);
            return null;
        }
    }

    /**
     * Get a medium representation of a book
     * @return The representation or null in case of errors
     */
    public Medium getMedium(String _bookName, ProcessOutputBuffer _out)
    {
        try {
            String xml = fetchBook(_bookName, _out);
            XMLNode root = new XMLParser(xml).parse();

            // Get attributes
            String url = "https://de.wikibooks.org/wiki/" + _bookName.strip().replace(" ","_");
            String title = root.get("page").get("title").getContent();
            if (title == null)
            {
                _out.write("Failed to get book title", Severity.ERROR);
                return null;
            }
            String tableOfContents = getTableOfContents(_out, root);
            if (tableOfContents == null)
            {
                _out.write("Failed to get book table of contents", Severity.ERROR);
                return null;
            }
            String contributor = getContributorName(_bookName, _out, xml);
            if (contributor == null)
            {
                _out.write("Failed to get book contributor name", Severity.ERROR);
                return null;
            }
            LocalDateTime timestamp = getRevisionTimestamp(_bookName, _out, xml);
            if (timestamp == null)
            {
                _out.write("Failed to get book timestamp", Severity.ERROR);
                return null;
            }
            String shelf = getShelf(_out, root);
            if (shelf == null)
            {
                _out.write("Failed to get book shelf", Severity.ERROR);
                return null;
            }
            // Build medium

            return new ElectronicalMediumWikiBooks()
                    .setTimestamp(timestamp)
                    .setContributor(contributor)
                    .setTableOfContents(tableOfContents)
                    .setShelf(shelf)
                    .setURL(url)
                    .setTitle(title);

        } catch (XMLParseException | URLUtils.URLException e) {
            _out.write("Failed to parse xml response: " + e.getMessage(), Severity.ERROR);
            return null;
        }
    }


}
