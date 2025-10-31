package Library.user_interface.cli_commands;

import Library.Collection;
import Library.Library;
import Library.Medium.Medium;
import Library.io.ProcessOutputBuffer;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;

import java.util.Arrays;

/**
* List all the mediums in the collection with some display options
* @author lkoeble 21487
*/
public class List implements ICLIEndpoint {
    @Override
    public void call(String[] params, ProcessOutputBuffer _out, CLI _cli) {
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
            for (Medium m : Library.collection)
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
            for (Medium m : Library.collection)
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
        return "cli-list-mediums";
    }
}
