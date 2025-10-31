package Library.user_interface.cli_commands;

import Library.Collection;
import Library.Library;
import Library.Medium.Medium;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;
import Library.utils.TextUtils;

import java.util.Arrays;
import java.util.stream.Stream;

/**
* Search  medium in the collection by title or id
* @author lkoeble 21487
*/
public class Search implements ICLIEndpoint {
    @Override
    public void call(String[] params, ProcessOutputBuffer _out, CLI _cli) {
        // Handle parameters empty
        if (params.length <= 0)
        {
            _out.write("Missing search parameter", Severity.ERROR);
            return;
        }

        // Handle case collection empty
        if (Library.collection.isEmpty())
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
                        result = Library.collection.findMedium(t, _out);

                    }else
                    {
                        if (ignoreCase)
                        {
                            result = Library.collection.findMedium(t, _out, false, true, true);
                        }
                        else
                        {
                            result = Library.collection.findMedium(t, _out, false, true, false);
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
                    Medium m = Library.collection.findMedium(Long.parseLong(title), _out);
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
        return "cli-search-mediums";
    }
}
