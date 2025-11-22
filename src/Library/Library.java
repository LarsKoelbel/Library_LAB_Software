package Library;

import Library.database.Server;
import Library.database.ServerAddressScopes;
import Library.io.*;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;
import Library.user_interface.cli_commands.*;

import java.util.ArrayList;

/**
 * Main class for managing the library
 * @author lkoelbel
 * @matnr 21487
 */
public class Library {

    public static Server server = null;
    public static final Archive collection = new Archive();

    public static void main(String[] argv)
    {

        final CLI cli = new CLI();

        // Register CLI commands
        {
            // List command - list all mediums
            cli.registerEndpoint(new List(), "list");
            // List search
            cli.registerEndpoint(new Search(), "search");
            // Modify status
            cli.registerEndpoint(new StatusSet(), "status set");
            // Add command - add new BibTex Medium
            cli.registerEndpoint(new Add(), "add");
            // Save binary
            cli.registerEndpoint(new SaveBinary(),"save binary");
            // Save bibtex
            cli.registerEndpoint(new SaveBibtex(), "save bibtex");
            // Load binary
            cli.registerEndpoint(new LoadBinary(),"load binary");
            // Load bibtex
            cli.registerEndpoint(new LoadBibtex(),"load bibtex");
            // Load server
            cli.registerEndpoint(new LoadDatabase(),"load database");
            // Clear list
            cli.registerEndpoint(new Clear(), "clear");
            // Drop
            cli.registerEndpoint(new Drop(), "drop");
            // Connect server
            cli.registerEndpoint(new ConnectDatabaseServer(), "connect database-server");
            // Disconnect server
            cli.registerEndpoint(new DisconnectDatabaseServer(), "disconnect database-server");
            // WikiBooks
            cli.registerEndpoint(new WikiBooks(), "wikibooks");
        }

        // Register CLI startup call
        cli.registerStartUpCall(new ICLIEndpoint() {
            @Override
            public void call(String[] params, ProcessOutputBuffer _out, CLI _cli) {
                _out.write("Checking server availability. This might take a while, please wait...");
                // Check server for all scopes not marked skip
                boolean serverAvailable = false;
                ArrayList<ServerAddressScopes> serverAddressScopesArrayList = new ArrayList<>();
                for (ServerAddressScopes a : ServerAddressScopes.values())
                {
                    if(a.searchOnStartup)
                    {
                        Server server1 = new Server("","", a.url);
                        _out.write("Searching on " + a.name);
                        _cli.flushOutputBuffer(_out);
                        if(server1.testConnection())
                        {
                            _out.write("Server available", Severity.SUCCESS);
                            serverAddressScopesArrayList.add(a);
                            serverAvailable = true;
                        }else
                        {
                            _out.write("Server not available", Severity.WARNING);
                        }
                        _cli.flushOutputBuffer(_out);
                    }
                }

                _out.nl();

                if(serverAvailable)
                {
                    StringBuilder sb = new StringBuilder();
                    for (ServerAddressScopes s : serverAddressScopesArrayList)
                    {
                        sb.append(s.name).append(", ");
                    }
                    _out.write("Server available on: "+ sb.substring(0,sb.length()-2) +" - use 'connect' to connect", Severity.SUCCESS);
                }else
                {
                    _out.write("Server not available - check internet connection", Severity.WARNING);
                }

                _out.write("Running in detached/ offline mode", Severity.REMARK);

                _out.write("\nType '?' for help and command overview");
            }

            @Override
            public String getProcessName() {
                return "cli-startup";
            }
        });

        // Start the cli

        cli.start();


    }

}
