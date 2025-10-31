package Library.user_interface.cli_commands;

import Library.Library;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;
import Library.utils.TextUtils;

/**
* The add command adds a new medium to the collection
* @author lkoeble 21487
*/
public class Add implements ICLIEndpoint {

    @Override
    public void call(String[] params, ProcessOutputBuffer _out, CLI _cli) {
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

            if(Library.collection.addMedium(b.strip(), _out)){
                _out.write("Done with: " + b, Severity.SUCCESS);
            }else
            {
                _out.write("Failed on: " + b, Severity.WARNING);
            }

            if(bibTex.length() > 1)
            {
                // Wait a little to make sure the server hase time to compute the changes
                _cli.flushOutputBuffer(_out);
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
        return "cli-add-medium";
    }
}
