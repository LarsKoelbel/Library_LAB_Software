package Library.user_interface;

import Library.io.Message;
import Library.io.ProcessOutputBuffer;
import Library.io.Severity;
import Library.utils.IColorCodes;
import Library.utils.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
* CLI interface for the library
* @author lkoeble 21487
*/
public class CLI {
    Node<String, ICLIEndpoint> root = new Node<>();

    private Scanner scanner = new Scanner(System.in);

    private boolean ACTIVE = false;

    private ICLIEndpoint startUpCall = null;

    private static final String VERSION = "1.04.3-alpha";



    /**
     * Add a new endpoint (command) to the cli
     * @param _endpoint Endpoint object
     * @param _path Path (command)
     */
    public void registerEndpoint(ICLIEndpoint _endpoint, String _path)
    {
        String[] path = _path.strip().replace("\n", "").split(" ");

        Node<String, ICLIEndpoint> current = root;

        for (String s : path)
        {
            current = current.proceedOrCreate(s);
        }

        current.set(_endpoint);
    }

    /**
     * Call a function from the CLI with a path (command) and options
     * @param _path Path and options
     * @param _out Output Buffer
     * @return Success status
     */
    private boolean call(String _path, ProcessOutputBuffer _out)
    {
        ArrayList<String> path = new ArrayList<>(Arrays.asList(_path.strip().replace("\n", "").split(" ")));

        int level = 0;
        Node<String, ICLIEndpoint> current = root;
        for (String s : path)
        {
            Node<String, ICLIEndpoint> next = current.proceed(s);

            if (next == null)
            {
                _out.write("Unknown command. Unknown keyword " + s, Severity.ERROR);
                return false;
            }
            if (next.hasContent())
            {
                String[] parms = path.subList(level + 1, path.size()).toArray(new String[0]);
                next.get().call(parms, _out);
                if (_out.getProcessName() == null) _out.setProcessName(next.get().getProcessName());
                return true;
            }

            current = next;
            level++;
        }

        _out.write("Unknown command.", Severity.ERROR);
        return false;
    }

    /**
     * Get a process output buffer formated ready for user output
     * @param _out The buffer
     * @return The formated string
     */
    private String getProcessOutputBufferFormated(ProcessOutputBuffer _out)
    {
        StringBuilder sb = new StringBuilder();

        for (Message m : _out.getAll())
        {
            switch (m.getSeverity())
            {
                case Severity.ERROR -> sb.append(TextUtils.style("ERROR: ", IColorCodes.RED + IColorCodes.BOLD)).append("\n").append(
                        TextUtils.style(m.toString(), IColorCodes.RED)
                        ).append("\n");
                case Severity.WARNING -> sb.append(TextUtils.style("WARNING: ", IColorCodes.YELLOW + IColorCodes.BOLD)).append(
                        TextUtils.style(m.toString(), IColorCodes.YELLOW)
                ).append("\n");
                case Severity.FATAL -> sb.append(TextUtils.style("FATAL: ", IColorCodes.RED + IColorCodes.BOLD)).append("\n").append(
                        TextUtils.style(m.toString(), IColorCodes.PURPLE)
                ).append("\n");
                case Severity.SUCCESS -> sb.append(
                        TextUtils.style(m.toString(), IColorCodes.BRIGHT_GREEN)
                ).append("\n");
                case Severity.REMARK -> sb.append(
                        TextUtils.style(m.toString(), IColorCodes.BRIGHT_PURPLE)
                ).append("\n");
                default -> sb.append(
                        m.toString()
                ).append("\n");
            }

        }

        return  sb.toString();
    }

    /**
     * Ask the user on the cli
     * @param _question Message to be displayed
     * @return The answer
     */
    public String ask(String _question)
    {
        System.out.print(_question);
        return scanner.nextLine();
    }

    /**
     * Start the CLI Interface
     */
    public void start()
    {
        start(false);
    }

    /**
     * Register a cli startup call
     * @param _call Endpoint to be called (not part of the tree)
     */
    public void registerStartUpCall(ICLIEndpoint _call)
    {
        startUpCall = _call;
    }

    /**
     * Register elementary commands
     * grep - filter output buffer
     */
    private void initElementaryCommands()
    {
        this.registerEndpoint(new ICLIEndpoint() {
            @Override
            public void call(String[] params, ProcessOutputBuffer _out) {
                if (params.length <= 0) return;
                StringBuilder sb = new StringBuilder();

                for (String x : params)
                {
                    sb.append(x).append(" ");
                }

                String regex = sb.toString().strip();

                ProcessOutputBuffer processOutputBuffer = new ProcessOutputBuffer("grep-operation::" + regex);

                for (Message m : _out.getAll())
                {
                    String[] lines = m.toString().split("\n");

                    for (String line : lines)
                    {
                        if (line.toLowerCase().contains(regex.toLowerCase())) {
                            processOutputBuffer.write(new Message(line, m.getSeverity()).setTimestamp(m.getTimestamp()));
                        }
                    }
                }

                _out.replace(processOutputBuffer);

            }

            @Override
            public String getProcessName() {
                return "grep-operation";
            }
        },"grep");

        this.registerEndpoint(new ICLIEndpoint() {
            @Override
            public void call(String[] params, ProcessOutputBuffer _out) {
                if (params.length <= 0) return;
                StringBuilder sb = new StringBuilder();

                for (String x : params)
                {
                    sb.append(x).append(" ");
                }

                String regex = sb.toString().strip();

                ProcessOutputBuffer processOutputBuffer = new ProcessOutputBuffer("block-grep-operation::" + regex);

                StringBuilder block = new StringBuilder();
                boolean goodBlock = false;
                Severity lastSeverity = null;
                long lastTimestamp = -1;

                for (Message m : _out.getAll())
                {
                    lastSeverity = m.getSeverity();
                    lastTimestamp = m.getTimestamp();
                    String[] lines = m.toString().split("\n");

                    for (String line : lines)
                    {
                        block.append(line).append("\n");
                        if (line.contains(regex)) {
                            goodBlock = true;
                        }
                        if (line.isBlank())
                        {
                            if (goodBlock)
                            {
                                processOutputBuffer.write(new Message(block.toString(), lastSeverity).setTimestamp(lastTimestamp));
                                goodBlock = false;
                            }
                            block = new StringBuilder();
                        }
                    }
                }

                if(!block.isEmpty() && goodBlock)
                {
                    processOutputBuffer.write(new Message(block.toString(), lastSeverity).setTimestamp(lastTimestamp));
                }

                _out.replace(processOutputBuffer);

            }

            @Override
            public String getProcessName() {
                return "block-grep-operation";
            }
        },"blocks with");

        this.registerEndpoint(new ICLIEndpoint() {
            @Override
            public void call(String[] params, ProcessOutputBuffer _out) {
                _out.write(ICLIHelpContainer.CLI_HELP);
            }

            @Override
            public String getProcessName() {
                return "cli-help";
            }
        }, "?");
    }

    /**
     * Flush the content of an output buffer directly to the console before the process has finished
     * @param _out Process output buffer to flush
     */
    public void flushOutputBuffer(ProcessOutputBuffer _out)
    {
        System.out.println(getProcessOutputBufferFormated(_out));
        _out.clear();
    }

    /**
     * Start the CLI Interface
     * @param _noExit Do not set up an exit command (not commanded)
     */
    public void start(boolean _noExit)
    {
        String promptStart = "> ";

        if (!_noExit)
        {
            // Exit command is automatically set up
            this.registerEndpoint(new ICLIEndpoint() {
                @Override
                public void call(String[] params, ProcessOutputBuffer _out) {
                    ACTIVE = false;
                }

                @Override
                public String getProcessName() {
                    return "cli.exit-process";
                }
            }, "exit");
        }

        // Init elementary commands like grep
        initElementaryCommands();

        System.out.println();
        System.out.println("Library CLI version: " + VERSION);
        System.out.println();

        // Run startup call if available

        if(startUpCall != null)
        {
            ProcessOutputBuffer out = new ProcessOutputBuffer("cli-startup");
            startUpCall.call(null, out);

            if(out.hasMessages())
            {
                System.out.println(getProcessOutputBufferFormated(out));

                // Check for fatal errors
                if(out.getMostSevere().getSeverity().getLevel() >= Severity.FATAL.getLevel()) return;
            }
        }

        ACTIVE = true;

        // Start loop
        while (ACTIVE)
        {
            System.out.print(promptStart);
            String command = scanner.nextLine();

            if (command.isBlank()) continue;

            // Handle chained commands
            String[] commands = command.split("\\|");

            ProcessOutputBuffer processOutputBuffer = new ProcessOutputBuffer(null);

            for (String c : commands)
            {
                c = c.strip();

                boolean success =  call(c, processOutputBuffer);
                if (processOutputBuffer.hasMessages())
                {
                    // Exit if error fatal
                    if(processOutputBuffer.getMostSevere().getSeverity().getLevel() >= Severity.FATAL.getLevel()){
                        System.out.println(getProcessOutputBufferFormated(processOutputBuffer));
                        return;
                    }
                }
            }

            System.out.println(getProcessOutputBufferFormated(processOutputBuffer));

        }


    }
}
