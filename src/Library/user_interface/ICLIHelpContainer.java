package Library.user_interface;

public interface ICLIHelpContainer {
    static final String CLI_HELP = "───────────────────────────────────────────────\n" +
            "\uD83D\uDCD6  LIBRARY MANAGEMENT CLI – COMMAND REFERENCE\n" +
            "───────────────────────────────────────────────\n" +
            "Manage your digital media library locally or through a connected database server.\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83E\uDDFE  LIST MEDIUMS\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    list [options]\n" +
            "Description:\n" +
            "    Lists all mediums currently in the local collection.\n" +
            "Options:\n" +
            "    -l              Long listing format (detailed view)\n" +
            "    -s              Short listing format (default)\n" +
            "    -b              Include BibTeX entry for each medium\n" +
            "    -d              Include database representation string\n" +
            "Examples:\n" +
            "    list -l\n" +
            "    list -s -b\n" +
            "    list -l -d\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDD0D  SEARCH MEDIUMS\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    search <selector> [options] <query>\n" +
            "Selectors:\n" +
            "    title           Search by title\n" +
            "    id              Search by inventory ID\n" +
            "Options:\n" +
            "    -l              Long listing format\n" +
            "    -s              Short listing format\n" +
            "    -b              Include BibTeX data\n" +
            "    -d              Include database string\n" +
            "    -e              Use exact match instead of substring\n" +
            "    -ignore-case    Ignore case in exact match\n" +
            "Note:\n" +
            "    You can provide multiple parameters if marked as <query>. For example,\n" +
            "    `search title \"Python\" \"AI\"` will search for all entries matching any\n" +
            "    of the given titles. Important: The drop command does NOT support\n" +
            "    multiple parameters.\n" +
            "Examples:\n" +
            "    search title -l Python\n" +
            "    search title -e -ignore-case \"Artificial Intelligence\"\n" +
            "    search title \"Python\" \"AI\"\n" +
            "    search id 42\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "➕  ADD MEDIUM\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    add <BibTeX_entry>\n" +
            "Description:\n" +
            "    Adds a new medium from a BibTeX formatted entry.\n" +
            "Note:\n" +
            "    You can add multiple entries at once by specifying multiple BibTeX\n" +
            "    entries in quotes. For example:\n" +
            "    add \"@book{python, title={Learning Python}, author={Mark Lutz}, year={2013}}\"\n" +
            "    \"@book{ai, title={AI Basics}, author={John Doe}, year={2020}}\"\n" +
            "Example:\n" +
            "    add @book{python, title={Learning Python}, author={Mark Lutz}, year={2013}}\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDCBE  SAVE COLLECTION\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    save binary <path|default>\n" +
            "    save bibtex <path|default>\n" +
            "Description:\n" +
            "    Saves the current collection either as a binary file (for internal use)\n" +
            "    or a BibTeX file (for export).\n" +
            "Examples:\n" +
            "    save binary my_library.bin\n" +
            "    save bibtex my_library.bib\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDCC2  LOAD COLLECTION\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    load binary <path|default>\n" +
            "    load bibtex <path|default>\n" +
            "Description:\n" +
            "    Loads a collection from a saved file. Confirmation is required if the\n" +
            "    current library is not empty.\n" +
            "Examples:\n" +
            "    load binary default\n" +
            "    load bibtex my_library.bib\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83C\uDF10  LOAD FROM DATABASE SERVER\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    load database\n" +
            "Description:\n" +
            "    Downloads all library data from the connected server. Not allowed if\n" +
            "    the local collection is not empty.\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83E\uDDF9  CLEAR LOCAL LIBRARY\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    clear\n" +
            "Description:\n" +
            "    Removes all mediums from the collection (after confirmation). Unsaved\n" +
            "    data will be lost.\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDDD1\uFE0F  DROP MEDIUM\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    drop <selector> [options] <value>\n" +
            "Selectors:\n" +
            "    title           Drop by title\n" +
            "    id              Drop by inventory ID\n" +
            "Options:\n" +
            "    -f              Force deletion of all matching entries\n" +
            "Note:\n" +
            "    The drop command does NOT support multiple parameters.\n" +
            "Examples:\n" +
            "    drop title \"Introduction to AI\"\n" +
            "    drop id 42\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDD17  CONNECT / DISCONNECT\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    connect database-server [scope]\n" +
            "    disconnect database-server\n" +
            "Description:\n" +
            "    Connects or disconnects from the remote database server. When\n" +
            "    connecting, the CLI will prompt for your username and password. You\n" +
            "    can specify a server scope to choose which server address is used for\n" +
            "    the connection.\n" +
            "Scopes:\n" +
            "    default         → Uses the default configured server.\n" +
            "    localhost       → Connects to a server running on this machine.\n" +
            "    local, local_network → Both connect to a server within the local network.\n" +
            "    global, global_1 → Both connect to the first global server.\n" +
            "    global_2       → Connects to the second global server.\n" +
            "Examples:\n" +
            "    connect database-server\n" +
            "    connect database-server localhost\n" +
            "    connect database-server local\n" +
            "    connect database-server local_network\n" +
            "    connect database-server global\n" +
            "    connect database-server global_1\n" +
            "    connect database-server global_2\n" +
            "    disconnect database-server\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "⚙\uFE0F  PIPELINING & FILTERING\n" +
            "───────────────────────────────────────────────\n" +
            "The CLI supports command chaining using | (pipe symbol). Multiple\n" +
            "commands connected with | share the same output buffer. The output of\n" +
            "the first command becomes the input of the next command.\n" +
            "Examples:\n" +
            "    list -l | grep \"Python\"\n" +
            "    list -l | blocks with \"AI\"\n" +
            "    search title \"Machine\" | grep \"author\"\n" +
            "    list -l | blocks with \"Journal\" | grep \"2020\"\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDD0E  GREP\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    grep <text>\n" +
            "Description:\n" +
            "    Filters the current process output and keeps only the lines that\n" +
            "    contain the specified text (case-insensitive).\n" +
            "Note:\n" +
            "    You can provide multiple parameters: grep \"Python\" \"AI\". Each\n" +
            "    parameter must be enclosed in quotes to be treated separately. Lines\n" +
            "    containing any of the provided texts will be kept.\n" +
            "Example:\n" +
            "    list -l | grep \"Python\" \"Machine Learning\"\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDCE6  BLOCKS WITH\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    blocks with <text>\n" +
            "Description:\n" +
            "    Filters complete message blocks that contain a specific text anywhere\n" +
            "    in the block. Paragraphs are kept as a single block (no completely\n" +
            "    empty lines), so you can filter by command sections or grouped content.\n" +
            "Note:\n" +
            "    You can provide multiple parameters: blocks with \"AI\" \"Python\". Each\n" +
            "    parameter must be enclosed in quotes to be treated separately. Blocks\n" +
            "    containing any of the provided texts will be kept.\n" +
            "Examples:\n" +
            "    list -l | blocks with \"AI\" \"Python\"\n" +
            "    search title \"Data\" | blocks with \"available\" \"checked out\"\n" +
            "    blocks with \"ADD MEDIUM\" \"DROP MEDIUM\" \"CONNECT / DISCONNECT\"\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "❓  HELP\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    ?\n" +
            "Description:\n" +
            "    Displays this help overview.\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDEF0\uFE0F  SUMMARY\n" +
            "───────────────────────────────────────────────\n" +
            "    list                    List all mediums\n" +
            "    search                  Find mediums by title or ID\n" +
            "    add                     Add new BibTeX entry\n" +
            "    save                    Save collection\n" +
            "    load                    Load collection\n" +
            "    load database           Load from connected server\n" +
            "    clear                   Clear local library\n" +
            "    drop                    Delete a specific medium\n" +
            "    connect / disconnect    Manage server connection\n" +
            "    grep                    Filter output lines\n" +
            "    blocks with             Filter grouped blocks\n" +
            "    ?                       Display command reference\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDCA1  TIP:\n" +
            "───────────────────────────────────────────────\n" +
            "You can combine commands using | to chain filters and view results\n" +
            "dynamically. Commands like search, grep, add, and blocks with also\n" +
            "support multiple parameters (except drop). For grep and blocks with,\n" +
            "each search parameter must be enclosed in quotes to be treated separately.\n" +
            "Paragraphs in this help are grouped without internal empty lines, so\n" +
            "you can use blocks with to filter the help for specific commands.\n" +
            "───────────────────────────────────────────────\n";
}
