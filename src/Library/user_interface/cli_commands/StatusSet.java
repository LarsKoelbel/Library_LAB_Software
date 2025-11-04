package Library.user_interface.cli_commands;

import Library.Library;
import Library.Medium.Medium;
import Library.Medium.Status;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.user_interface.CLI;
import Library.user_interface.ICLIEndpoint;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
* Set the status of a medium (checked_out or available)
* @author lkoeble 21487
*/
public class StatusSet implements ICLIEndpoint {

    @Override
    public void call(String[] params, ProcessOutputBuffer _out, CLI _cli) {
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
            Medium medium = Library.collection.findMedium(id, _out);
            if(medium != null)
            {
                // Get status
                if (params.length <= 1)
                {
                    _out.write("Missing status", Severity.ERROR);
                    return;
                }

                Status status = null;

                if("available".contains(params[1].toLowerCase())) status = Status.AVAILABLE;
                else if ("checked-out".contains(params[1].toLowerCase())) status = Status.CHECKED_OUT;

                if (status == null)
                {
                    _out.write("Status unknown: " + params[1], Severity.ERROR);
                    return;
                }

                LocalDate date = null;
                // Check for date
                if(status == Status.CHECKED_OUT)
                {
                    if (params.length < 3)
                    {
                        _out.write("No return date given", Severity.ERROR);
                        return;
                    }else
                    {
                        // Check the date
                        try
                        {
                            date = LocalDate.parse(params[2]);

                            if (date.isBefore(LocalDate.now()))
                            {
                                _out.write("The provided return date is in the past", Severity.ERROR);
                                return;
                            }
                        }catch (DateTimeParseException e)
                        {
                            _out.write("The provided date was not in the right format YYYY-MM-DD", Severity.ERROR);
                            return;
                        }
                    }

                }

                boolean completeOnServerSide = false;

                switch (status)
                {
                    case Status.CHECKED_OUT -> completeOnServerSide = medium.checkOut(date, _out);
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
        return "cli-status-set";
    }
}
