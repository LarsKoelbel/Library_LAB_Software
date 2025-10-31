package Library.user_interface.cli_commands;

import Library.Collection;
import Library.Library;
import Library.database.Server;
import Library.io.Communication;
import Library.io.IExceptionUserReadable;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;

/**
* Load collection data from the database
* @author lkoeble 21487
*/
public class LoadDatabase implements ICLIEndpoint {

    @Override
    public void call(String[] params, ProcessOutputBuffer _out, CLI _cli) {

        Server server = Library.server;
        Collection collection = Library.collection;

        if (server == null || !server.testAuth(Communication.NULL_BUFFER))
        {
            _out.write("Server not connected or server connection lost. Please use 'connect database-server' to (re)connect", Severity.ERROR);
            return;
        }

        try
        {
            // Check if the collection is empty
            if(collection.isEmpty())
            {
                Collection c = server.getCollectionFromDatabase(_out);
                if(c != null)
                {
                    if (c.isEmpty())
                    {
                        _out.write("No data on server", Severity.REMARK);
                    }else
                    {
                        collection.merge(c);
                        _out.write("Data download complete", Severity.SUCCESS);
                    }
                }
            }else
            {
                _out.write("Your current library is not empty. Loading from server in this state is not allowed out of risk to the database integrity. Merging local changes into the database is not jet supportet.\n" +
                        "The recommended procedure is to save your changed locally and clear using 'clear'", Severity.WARNING);
            }

        }catch (Exception e)
        {
            if (e instanceof IExceptionUserReadable)
            {
                _out.write(((IExceptionUserReadable) e).getUserMessage(), Severity.ERROR);
            }
            else
            {
                _out.write("Fatal error while loading: " + e.getMessage(), Severity.FATAL);
            }
        }

    }

    @Override
    public String getProcessName() {
        return "cli-load-server";
    }

}
