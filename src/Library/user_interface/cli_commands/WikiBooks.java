package Library.user_interface.cli_commands;

import Library.Library;
import Library.Medium.Medium;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;
import Library.xml.WikiBookParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
* Handler class for the cli command 'wikibooks'
* @author lkoeble 21487
*/
public class WikiBooks implements ICLIEndpoint {

    @Override
    public void call(String[] params, ProcessOutputBuffer _out, CLI _cli) {
        if (params.length <= 0)
        {
            _out.write("Not enough parameters for the WikiBooks command", Severity.ERROR);
            return;
        }
        switch (params[0])
        {
            case "fetch-contributor" -> fetch_contributor(params, _out, _cli);
            case "fetch" -> fetch(params, _out, _cli);
            default -> {
                _out.write("Unknown operation '"+ params[0] +"'", Severity.ERROR);
                return;
            }
        }
    }

    @Override
    public String getProcessName() {
        return "cli-wiki-books";
    }

    private static void fetch_contributor(String[] params, ProcessOutputBuffer _out, CLI _cli)
    {
        if (params.length <= 1)
        {
            _out.write("No book name specified", Severity.ERROR);
            return;
        }

        // Fetch xml
        WikiBookParser parser = new WikiBookParser();
        String name = parser.getContributorName(params[1], _out);
        if (name != null)
        {
            _out.write("Last contributor for this page: " + name, Severity.SUCCESS);
        }
        // Fetch revision timestamp
        LocalDateTime timestamp = parser.getRevisionTimestamp(params[1], _out);
        if (timestamp != null)
        {

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            _out.write("Last contribution on: " + timestamp.format(fmt), Severity.SUCCESS);
        }
    }

    private static void fetch(String[] params, ProcessOutputBuffer _out, CLI _cli)
    {
        if (params.length <= 1)
        {
            _out.write("No book name specified", Severity.ERROR);
            return;
        }

        // Fetch xml
        WikiBookParser parser = new WikiBookParser();
        // Get the medium
        Medium medium = parser.getMedium(params[1], _out);
        if (medium == null) return;
        Library.collection.addMedium(medium, _out);
        _out.write("Added '"+ medium.getTitle() +"' to archive", Severity.SUCCESS);
    }
}
