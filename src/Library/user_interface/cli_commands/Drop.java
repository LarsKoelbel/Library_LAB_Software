package Library.user_interface.cli_commands;

import Library.Library;
import Library.database.Server;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;
import Library.utils.DuplicateEntryException;
import Library.Collection;

import java.util.Arrays;

/**
* Drop a medium (or multiple) from the collection by title or id
* @author lkoeble 21487
*/
public class Drop implements ICLIEndpoint {

    @Override
    public void call(String[] params, ProcessOutputBuffer _out, CLI _cli) {

        Collection collection = Library.collection;
        Server server = Library.server;

        // Check server status
        if(server != null)
        {
            if(!_cli.ask(
                    "The session is currently connected to a server. All drop operations will reflect to the servers database and can not be recovered. Are you sure yu want to proceed? [y/n]"
            ).equalsIgnoreCase("y"))
            {
                _out.write("Operation aborted. No items where dropped", Severity.REMARK);
                return;
            }
        }

        // Check parms
        if (params.length <= 0)
        {
            _out.write("No selector found. Use 'title' or 'id'", Severity.ERROR);
            return;
        }

        // Process options
        // Options:
        //      -f - force deletion of all objects

        boolean force = Arrays.asList(params).contains("-f");

        switch (params[0]){
            case "title":
            {
                // Get title from params
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i<params.length; i++)
                {
                    if(!params[i].equals("-f")) sb.append(params[i]).append(" ");
                }

                String title = sb.toString().strip();

                try
                {
                    collection.dropMedium(title, _out, force);
                }catch (DuplicateEntryException e)
                {
                    _out.write("The title provided matches multiple mediums. Use -f to remove all or select by id.\n" + e.getUserMessage(), Severity.REMARK);
                    return;
                }
                break;
            }
            case "id":
            {
                try {
                    collection.dropMedium(Long.parseLong(params[1]), _out);
                }catch (NumberFormatException e)
                {
                    _out.write("The id is not a valid number. Use 'title' selector to drop by title", Severity.ERROR);
                    return;
                }
                break;
            }
            default:
            {
                _out.write("Invalid selector: " + params[0], Severity.ERROR);
                return;
            }
        }
    }

    @Override
    public String getProcessName() {
        return "cli-drop-medium";
    }

}
