package Library;

import Library.Medium.Medium;
import Library.Medium.Status;
import Library.bib_tex.BibTexParser;
import Library.database.Server;
import Library.database.ServerAddressScopes;
import Library.io.*;
import Library.persistency.BibTexPersistency;
import Library.persistency.BinaryPersistency;
import Library.persistency.HumanReadablePersistency;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;
import Library.utils.DuplicateEntryException;
import Library.utils.TextUtils;

import java.lang.reflect.Array;
import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Main class for managing the library
 * @author lkoelbel
 * @matnr 21487
 */
public class Library {

    private static final String DEFAULT_BINARY_FILE = "src/Library/data/objects.lib.bin";
    private static final String DEFAULT_BIBTEX_FILE = "src/Library/data/objects.lib.bibtex";

    public static Server server = null;

    public static void main(String[] argv)
    {
        // Create a new library collection
        final Collection collection = new Collection();

        // Create cli
        final CLI cli = new CLI();

        // Register CLI commands
        {
            // List command - list all mediums
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
                    // Handle case collection empty
                    if (collection.isEmpty())
                    {
                        _out.write("No data yet");
                        return;
                    }

                    // Handel options
                    // Options:
                    //          -l - List long format
                    //          -s - list short format
                    //          -b - list bibtex
                    //          -d - show database string

                    boolean listLong = false;
                    boolean listBibtex = false;
                    boolean listDatabase = false;

                    if(params.length > 0)
                    {
                        if(Arrays.asList(params).contains("-l")) listLong = true;
                        if(Arrays.asList(params).contains("-s")) listLong = false;
                        if(Arrays.asList(params).contains("-b")) listBibtex = true;
                        if(Arrays.asList(params).contains("-d")) listDatabase = true;
                    }

                    // Generate the output

                    StringBuilder sb = new StringBuilder();

                    if (listLong)
                    {
                        for (Medium m : collection)
                        {
                            sb.append(m.generateRepresentation()).append("\n\n");

                            if (listBibtex)
                            {
                                sb.append(m.getBibtex().getBibTexString()).append("\n\n");
                            }
                            if (listDatabase)
                            {
                                sb.append(Collection.getDataBaseString(m)).append("\n\n");
                            }
                        }
                    }else
                    {
                        for (Medium m : collection)
                        {
                            sb.append(m.generateShortRepresentation()).append("\n");

                            if (listBibtex)
                            {
                                sb.append("\t").append(m.getBibtex().getBibTexString()).append("\n");
                            }
                            if (listDatabase)
                            {
                                sb.append("\t").append(Collection.getDataBaseString(m)).append("\n");
                            }
                        }
                    }

                    // Write everything to the buffer

                    _out.write(sb.toString());

                }

                @Override
                public String getProcessName() {
                    return "list-mediums";
                }
            }, "list");

            // List search
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
                    // Handle parameters empty
                    if (params.length <= 0)
                    {
                        _out.write("Missing search parameter", Severity.ERROR);
                        return;
                    }

                    // Handle case collection empty
                    if (collection.isEmpty())
                    {
                        _out.write("No data yet");
                        return;
                    }

                    // Handel options
                    // Options:
                    //          -l - List long format
                    //          -s - list short format
                    //          -b - list bibtex
                    //          -d - show database string
                    //          -e - exact search
                    //          -ignore-case - ignore case in exact search

                    boolean listLong = false;
                    boolean listBibtex = false;
                    boolean listDatabase = false;
                    boolean exactSearch = false;
                    boolean ignoreCase = false;

                    if(params.length > 0)
                    {
                        if(Arrays.asList(params).contains("-l")) listLong = true;
                        if(Arrays.asList(params).contains("-s")) listLong = false;
                        if(Arrays.asList(params).contains("-b")) listBibtex = true;
                        if(Arrays.asList(params).contains("-d")) listDatabase = true;
                        if(Arrays.asList(params).contains("-e")) exactSearch = true;
                        if(Arrays.asList(params).contains("-ignore-case")) ignoreCase = true;
                    }

                    // Get title from params
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i<params.length; i++)
                    {
                        if(!params[i].contains("-l")
                                && !params[i].equals("-s")
                                && !params[i].equals("-b")
                                && !params[i].equals("-e")
                                && !params[i].equals("-ignore-case")
                                && !params[i].equals("-d")) sb.append(params[i]).append(" ");
                    }

                    String title = sb.toString().strip();

                    if (title.isBlank())
                    {
                        _out.write("Missing search parameter", Severity.ERROR);
                        return;
                    }



                    // Search collection
                    Medium[] mediums = null;
                    switch (params[0])
                    {
                        case "title":
                        {
                            // Check for substrings
                            String[] substrings = TextUtils.getSubstrings(title);
                            String[] titles;
                            if (substrings != null) titles = substrings;
                            else titles = new String[]{title};

                            for (String t : titles)
                            {
                                Medium[] result;
                                if (!exactSearch)
                                {
                                    result = collection.findMedium(t, _out);

                                }else
                                {
                                    if (ignoreCase)
                                    {
                                        result = collection.findMedium(t, _out, false, true, true);
                                    }
                                    else
                                    {
                                        result = collection.findMedium(t, _out, false, true, false);
                                    }
                                }

                                if (mediums != null && result != null) mediums = Stream.concat(Arrays.stream(mediums), Arrays.stream(result)).toArray(Medium[]::new);
                                else mediums = result;
                            }

                            break;
                        }
                        case "id":
                        {
                            try
                            {
                                Medium m = collection.findMedium(Long.parseLong(title), _out);
                                if (m != null) mediums = new Medium[] {m};
                                break;
                            }catch (NumberFormatException e)
                            {
                                _out.write("Invalid numerical value for id: " + title, Severity.ERROR);
                                return;
                            }

                        }
                    }

                    // Generate the output

                    if (mediums == null || mediums.length <= 0)
                    {
                        _out.write("No mediums found");
                        return;
                    }

                    sb = new StringBuilder();

                    if (listLong)
                    {
                        for (Medium m : mediums)
                        {
                            sb.append(m.generateRepresentation()).append("\n\n");

                            if (listBibtex)
                            {
                                sb.append(m.getBibtex().getBibTexString()).append("\n\n");
                            }
                            if (listDatabase)
                            {
                                sb.append(Collection.getDataBaseString(m)).append("\n\n");
                            }
                        }
                    }else
                    {
                        for (Medium m : mediums)
                        {
                            sb.append(m.generateShortRepresentation()).append("\n");

                            if (listBibtex)
                            {
                                sb.append("\t").append(m.getBibtex().getBibTexString()).append("\n");
                            }
                            if (listDatabase)
                            {
                                sb.append("\t").append(Collection.getDataBaseString(m)).append("\n");
                            }
                        }
                    }

                    // Write everything to the buffer

                    _out.write(sb.toString());

                }

                @Override
                public String getProcessName() {
                    return "search-mediums";
                }
            }, "search");

            // Modify status
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
                    // Check id
                    if (params.length <= 0)
                    {
                        _out.write("Missing id", Severity.ERROR);
                        return;
                    }

                    try
                    {
                        long id = Long.parseLong(params[0]);

                        // Find medium
                        Medium medium = collection.findMedium(id, _out);
                        if(medium != null)
                        {
                            // Get status
                            if (params.length <= 1)
                            {
                                _out.write("Missing status", Severity.ERROR);
                                return;
                            }

                            Status status = null;

                            if(params[1].toLowerCase().contains("available")) status = Status.AVAILABLE;
                            else if (params[1].toLowerCase().contains("checked-out")) status = Status.CHECKED_OUT;

                            if (status == null)
                            {
                                _out.write("Status unknown: " + params[1], Severity.ERROR);
                                return;
                            }

                            boolean completeOnServerSide = false;

                            switch (status)
                            {
                                case Status.CHECKED_OUT -> completeOnServerSide = medium.checkOut(null, _out);
                                case Status.AVAILABLE -> completeOnServerSide = medium.giveBack(_out);
                            }

                            if(!completeOnServerSide) return;
                            else {
                                _out.write("Status changed: " + status, Severity.SUCCESS);
                            }
                        }else
                        {
                            _out.write("Medium not found", Severity.ERROR);
                            return;
                        }

                    }catch (NumberFormatException e)
                    {
                        _out.write("Id is not a valid numerical value: " + params[0], Severity.ERROR);
                        return;
                    }


                }

                @Override
                public String getProcessName() {
                    return "cli-modify-status";
                }
            }, "status set");

            // Add command - add new BibTex Medium
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
                    // Handle case parms empty
                    if(params.length <= 0)
                    {
                        _out.write("No BibTex provided.", Severity.ERROR);
                        return;
                    }

                    // Combine all parameters into one string
                    StringBuilder bibTex = new StringBuilder();

                    for (String x : params)
                    {
                        bibTex.append(x).append(" ");
                    }

                    String[] bibtex = new String[] {bibTex.toString()};

                    // Check for substrings
                    String[] substrings = TextUtils.getSubstrings(bibtex[0]);
                    if (substrings != null)  bibtex = substrings;

                    for (String b : bibtex)
                    {
                        // Syntax check
                        b = b.replace("'", "\\'");

                        if(collection.addMedium(b.strip(), _out)){
                            _out.write("Done with: " + b, Severity.SUCCESS);
                        }else
                        {
                            _out.write("Failed on: " + b, Severity.WARNING);
                        }

                        if(bibTex.length() > 1)
                        {
                            // Wait a little to make sure the server hase time to compute the changes
                            cli.flushOutputBuffer(_out);
                            //try {
                                //Thread.sleep(500);
                            //} catch (InterruptedException e) {
                            //    return;
                            //}
                        }
                    }


                }

                @Override
                public String getProcessName() {
                    return "add-medium";
                }
            }, "add");

            // Save binary
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
                    // Check path
                    if (params.length <= 0)
                    {
                        _out.write("No path provided", Severity.ERROR);
                        return;
                    }

                    if (params[0].equalsIgnoreCase("default")) params[0] = DEFAULT_BINARY_FILE;

                    BinaryPersistency binaryPersistency = new BinaryPersistency();

                    try
                    {
                        binaryPersistency.save(collection, params[0]);
                    }catch (Exception e)
                    {
                        if (e instanceof IExceptionUserReadable)
                        {
                            _out.write(((IExceptionUserReadable) e).getUserMessage(), Severity.ERROR);
                        }
                        else
                        {
                            _out.write("Fatal error while saving: " + e.getMessage(), Severity.FATAL);
                        }
                    }

                    _out.write("Saved under: " + params[0], Severity.SUCCESS);
                }

                @Override
                public String getProcessName() {
                    return "save-binary";
                }
            }, "save binary");

            // Save bibtex
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
                    // Check path
                    if (params.length <= 0)
                    {
                        _out.write("No path provided", Severity.ERROR);
                        return;
                    }

                    if (params[0].equalsIgnoreCase("default")) params[0] = DEFAULT_BIBTEX_FILE;

                    BibTexPersistency bibTexPersistency = new BibTexPersistency();

                    try
                    {
                        bibTexPersistency.save(collection, params[0]);
                    }catch (Exception e)
                    {
                        if (e instanceof IExceptionUserReadable)
                        {
                            _out.write(((IExceptionUserReadable) e).getUserMessage(), Severity.ERROR);
                        }
                        else
                        {
                            _out.write("Fatal error while saving: " + e.getMessage(), Severity.FATAL);
                        }
                    }

                    _out.write("Saved under: " + params[0], Severity.SUCCESS);
                }

                @Override
                public String getProcessName() {
                    return "save-bib-tex";
                }
            }, "save bibtex");

            // Load binary
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
                    if (server != null)
                    {
                        _out.write("The current session is or was connected to a server. Database integrity does not allow to load local data in unclear" +
                                " operational state. To load local data, please officially disconnect from the server using 'disconnect' command", Severity.WARNING);
                        return;
                    }

                    // Check path
                    if (params.length <= 0)
                    {
                        _out.write("No path provided", Severity.ERROR);
                        return;
                    }

                    if (params[0].equalsIgnoreCase("default")) params[0] = DEFAULT_BINARY_FILE;

                    try
                    {

                        // Check if the collection is empty
                        if(collection.isEmpty())
                        {
                            BinaryPersistency b = new BinaryPersistency();
                            Collection c = b.load(params[0]);
                            collection.merge(c);

                            _out.write("Done", Severity.SUCCESS);
                            return;
                        }else
                        {
                            if(cli.ask(
                                    "Your current library is not empty. Loading from a file now might lead to duplicate IDs. Are you sure you want to continue? [y/n]"
                            ).strip().equalsIgnoreCase("y"))
                            {
                                BinaryPersistency b = new BinaryPersistency();
                                Collection c = b.load(params[0]);
                                collection.merge(c);

                                _out.write("Done", Severity.SUCCESS);
                            }else
                            {
                                _out.write("Loading canceled", Severity.REMARK);
                            }
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
                    return "load-binary";
                }
            },"load binary");

            // Load bibtex
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
                    if (server != null)
                    {
                        _out.write("The current session is or was connected to a server. Database integrity does not allow to load local data in unclear" +
                                " operational state. To load local data, please officially disconnect from the server using 'disconnect' command", Severity.WARNING);
                        return;
                    }

                    // Check path
                    if (params.length <= 0)
                    {
                        _out.write("No path provided", Severity.ERROR);
                        return;
                    }

                    if (params[0].equalsIgnoreCase("default")) params[0] = DEFAULT_BIBTEX_FILE;

                    try
                    {

                        // Check if the collection is empty
                        if(collection.isEmpty())
                        {
                            BibTexPersistency b = new BibTexPersistency();
                            Collection c = b.load(params[0]);
                            collection.merge(c);

                            _out.write("Done", Severity.SUCCESS);
                            return;
                        }else
                        {
                            if(cli.ask(
                                    "Your current library is not empty. Loading from a file now might lead to duplicate IDs. Are you sure you want to continue? [y/n]"
                            ).strip().equalsIgnoreCase("y"))
                            {
                                BibTexPersistency b = new BibTexPersistency();
                                Collection c = b.load(params[0]);
                                collection.merge(c);

                                _out.write("Done", Severity.SUCCESS);
                            }else
                            {
                                _out.write("Loading canceled", Severity.REMARK);
                            }
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
                    return "load-bibtex";
                }
            },"load bibtex");

            // Load server
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {

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
                    return "load-server";
                }
            },"load database");

            // Clear list
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
                    if(cli.ask(
                            "This operation will remove all data not saved to the disk. Continue? [y/n]"
                    ).equalsIgnoreCase("y"))
                    {
                        collection.clear();
                        _out.write("Library data was cleared", Severity.SUCCESS);
                    }else
                    {
                        _out.write("Process canceled. No data was removed", Severity.REMARK);
                    }
                }

                @Override
                public String getProcessName() {
                    return "clear-library";
                }
            }, "clear");

            // Drop
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
                    // Check server status
                    if(server != null)
                    {
                        if(!cli.ask(
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
                    return "drop-medium";
                }
            }, "drop");

            // Connect server
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
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


                    String username = cli.ask("Enter username: ");
                    String password = cli.ask("Password for " + username + ": ");

                    server = new Server(username, password, scope.url);

                    if(server.testAuth(_out))
                    {
                        _out.write("Server connection ok", Severity.SUCCESS);
                    }else
                    {
                        server = null;
                        _out.write("Server connection failed on the scope: " + scope + ". Maybe try a different scope...", Severity.REMARK);
                    }
                }

                @Override
                public String getProcessName() {
                    return "server-connection";
                }
            }, "connect database-server");

            // Disconnect server
            cli.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {

                    if (server != null)
                    {
                        server = null;
                        _out.write("Disconnected from database-server", Severity.SUCCESS);
                        _out.write("It is currently not supported to merge local changed into the server database. All changes made now will be lost, unless saved locally.", Severity.WARNING);
                    }
                    else
                    {
                        _out.write("No server connected", Severity.REMARK);
                    }
                }

                @Override
                public String getProcessName() {
                    return "server-disconnection";
                }
            }, "disconnect database-server");
        }

        // Register CLI startup call
        cli.registerStartUpCall(new ICLIEndpoint() {
            @Override
            public void call(String[] params, ProcessOutputBuffer _out) {
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
                        cli.flushOutputBuffer(_out);
                        if(server1.testConnection())
                        {
                            _out.write("Server available", Severity.SUCCESS);
                            serverAddressScopesArrayList.add(a);
                            serverAvailable = true;
                        }else
                        {
                            _out.write("Server not available", Severity.WARNING);
                        }
                        cli.flushOutputBuffer(_out);
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
