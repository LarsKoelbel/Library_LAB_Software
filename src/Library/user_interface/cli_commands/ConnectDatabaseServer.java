package Library.user_interface.cli_commands;

import Library.Library;
import Library.database.Server;
import Library.database.ServerAddressScopes;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;

/**
* Connect to a database server
* @author lkoeble 21487
*/
public class ConnectDatabaseServer implements ICLIEndpoint {

    @Override
    public void call(String[] params, ProcessOutputBuffer _out, CLI _cli) {
        // Handle scope
        ServerAddressScopes scope = ServerAddressScopes.DEFAULT;
        if (params.length > 0)
        {
            switch (params[0])
            {
                case "default" -> {}
                case "localhost" -> scope = ServerAddressScopes.LOCAL_HOST;
                case "local", "local_network" -> scope = ServerAddressScopes.LOCAL_NETWORK;
                case "global", "global_1" -> scope = ServerAddressScopes.GLOBAL_1;
                case "global_2" -> scope = ServerAddressScopes.GLOBAL_2;
                default -> {
                    _out.write("The selected server scope is not valid: " + params[0], Severity.ERROR);
                    return;
                }
            }
        }


        String username = _cli.ask("Enter username: ");
        String password = _cli.ask("Password for " + username + ": ");

        Library.server = new Server(username, password, scope.url);

        if(Library.server.testAuth(_out))
        {
            _out.write("Server connection ok", Severity.SUCCESS);
        }else
        {
            Library.server = null;
            _out.write("Server connection failed on the scope: " + scope + ". Maybe try a different scope...", Severity.REMARK);
        }
    }

    @Override
    public String getProcessName() {
        return "cli-server-connection";
    }

}
