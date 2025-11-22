package Library.test;

import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.xml.WikiBookParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
* Test class for the WikiBooks fetcher and parser
* @author lkoeble 21487
*/
public class WikiBooksTest {
    public static void main(String[] argv)
    {
        if (argv.length <= 0)
        {
            System.out.println("FAILED: No book name specified");
            return;
        }

        ProcessOutputBuffer processOutputBuffer = new ProcessOutputBuffer("test-wiki-books-contributor-fetch");

        WikiBookParser wikiBookParser = new WikiBookParser();
        String name = wikiBookParser.getContributorName(argv[0], processOutputBuffer);

        if (name != null)
        {
            System.out.println("PASS: Response is :: " + name);
        }
        else
        {
            System.out.println("FAILED - Output buffer");
            System.out.println(processOutputBuffer);
        }

        // Fetch revision timestamp
        LocalDateTime timestamp = wikiBookParser.getRevisionTimestamp(argv[0], processOutputBuffer);
        if (timestamp != null)
        {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("PASS: Response is :: " + timestamp.format(fmt));
        }
        else
        {
            System.out.println("FAILED - Output buffer");
            System.out.println(processOutputBuffer);
        }
    }
}
