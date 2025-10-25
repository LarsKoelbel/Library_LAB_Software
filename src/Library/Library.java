package Library;

import Library.Medium.Medium;
import Library.bib_tex.BibTexParser;
import Library.io.*;
import Library.persistency.BibTexPersistency;
import Library.persistency.BinaryPersistency;
import Library.persistency.HumanReadablePersistency;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;
import Library.utils.DuplicateEntryException;

import java.lang.reflect.Array;
import java.rmi.ServerError;
import java.util.Arrays;

/**
 * Main class for managing the library
 * @author lkoelbel
 * @matnr 21487
 */
public class Library {

    private static final String DEFAULT_BINARY_FILE = "src/Library/data/objects.lib.bin";
    private static final String DEFAULT_BIBTEX_FILE = "src/Library/data/objects.lib.bibtex";

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

                    boolean listLong = false;
                    boolean listBibtex = false;

                    if(params.length > 0)
                    {
                        if(Arrays.asList(params).contains("-l")) listLong = true;
                        if(Arrays.asList(params).contains("-s")) listLong = false;
                        if(Arrays.asList(params).contains("-b")) listBibtex = true;
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

                    if(collection.addMedium(bibTex.toString().strip(), _out)){
                        _out.write("Done", Severity.SUCCESS);
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
                                if(!params[i].contains("-f")) sb.append(params[i]).append(" ");
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
        }

        // Start the cli

        cli.start();


    }

}
