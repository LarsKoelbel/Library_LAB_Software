package Library.user_interface.cli_commands;

import Library.Library;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;

/**
* Clear the collection from all medium data
* @author lkoeble 21487
*/
public class Clear implements ICLIEndpoint {

    @Override
    public void call(String[] params, ProcessOutputBuffer _out, CLI _cli) {
        if(_cli.ask(
                "This operation will remove all data not saved to the disk. Continue? [y/n]"
        ).equalsIgnoreCase("y"))
        {
            Library.collection.clear();
            _out.write("Library data was cleared", Severity.SUCCESS);
        }else
        {
            _out.write("Process canceled. No data was removed", Severity.REMARK);
        }
    }

    @Override
    public String getProcessName() {
        return "cli-clear-library";
    }

}
