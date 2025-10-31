package Library.user_interface.cli_commands;

import Library.Library;
import Library.io.IExceptionUserReadable;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.persistency.BinaryPersistency;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;

/**
* Save the collection as binary representation
* @author lkoeble 21487
*/
public class SaveBinary implements ICLIEndpoint {

    public static final String DEFAULT_BINARY_FILE = "src/Library/data/objects.lib.bin";

    @Override
    public void call(String[] params, ProcessOutputBuffer _out, CLI _cli) {
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
            binaryPersistency.save(Library.collection, params[0]);
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
        return "cli-save-binary";
    }
}
