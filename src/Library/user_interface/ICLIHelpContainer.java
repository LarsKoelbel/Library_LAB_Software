package Library.user_interface;

public interface ICLIHelpContainer {
    static final String CLI_HELP = "───────────────────────────────────────────────\n" +
            "\uD83D\uDCD8  LIBRARY MANAGEMENT CLI – COMMAND REFERENCE\n" +
            "───────────────────────────────────────────────\n" +
            "\n" +
            "Manage your digital media library locally or through\n" +
            "a connected database server.\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83E\uDDFE  LIST MEDIUMS\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    list [options]\n" +
            "\n" +
            "Description:\n" +
            "    Lists all mediums currently in the local collection.\n" +
            "\n" +
            "Options:\n" +
            "    -l              Long listing format (detailed view)\n" +
            "    -s              Short listing format (default)\n" +
            "    -b              Include BibTeX entry for each medium\n" +
            "    -d              Include database representation string\n" +
            "\n" +
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
            "\n" +
            "Selectors:\n" +
            "    title           Search by title\n" +
            "    id              Search by inventory ID\n" +
            "\n" +
            "Options:\n" +
            "    -l              Long listing format\n" +
            "    -s              Short listing format\n" +
            "    -b              Include BibTeX data\n" +
            "    -d              Include database string\n" +
            "    -e              Use exact match instead of substring\n" +
            "    -ignore-case    Ignore case in exact match\n" +
            "\n" +
            "Examples:\n" +
            "    search title -l Python\n" +
            "    search title -e -ignore-case \"Artificial Intelligence\"\n" +
            "    search id 42\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "➕  ADD MEDIUM\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    add <BibTeX_entry>\n" +
            "\n" +
            "Description:\n" +
            "    Adds a new medium from a BibTeX formatted entry.\n" +
            "\n" +
            "Example:\n" +
            "    add @book{python, title={Learning Python}, author={Mark Lutz}, year={2013}}\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDCBE  SAVE COLLECTION\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    save binary <path|default>\n" +
            "    save bibtex <path|default>\n" +
            "\n" +
            "Description:\n" +
            "    Saves the current collection either as:\n" +
            "      • Binary file (for internal use)\n" +
            "      • BibTeX file (for export)\n" +
            "\n" +
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
            "\n" +
            "Description:\n" +
            "    Loads a collection from a saved file.\n" +
            "    Confirmation required if current library is not empty.\n" +
            "\n" +
            "Examples:\n" +
            "    load binary default\n" +
            "    load bibtex my_library.bib\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83C\uDF10  LOAD FROM DATABASE SERVER\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    load database\n" +
            "\n" +
            "Description:\n" +
            "    Downloads all library data from the connected server.\n" +
            "    Not allowed if the local collection is not empty.\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83E\uDDF9  CLEAR LOCAL LIBRARY\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    clear\n" +
            "\n" +
            "Description:\n" +
            "    Removes all mediums from the collection (after confirmation).\n" +
            "    Unsaved data will be lost.\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDDD1\uFE0F  DROP MEDIUM\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    drop <selector> [options] <value>\n" +
            "\n" +
            "Selectors:\n" +
            "    title           Drop by title\n" +
            "    id              Drop by inventory ID\n" +
            "\n" +
            "Options:\n" +
            "    -f              Force deletion of all matching entries\n" +
            "\n" +
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
            "\n" +
            "Description:\n" +
            "    Connects or disconnects from the remote database server.\n" +
            "    When connecting, the CLI will prompt for your username\n" +
            "    and password.\n" +
            "\n" +
            "    You can specify a **server scope** to choose which server\n" +
            "    address is used for the connection.\n" +
            "\n" +
            "Scopes:\n" +
            "    default\n" +
            "        → Uses the default configured server.\n" +
            "\n" +
            "    localhost\n" +
            "        → Connects to a server running on this machine.\n" +
            "\n" +
            "    local, local_network\n" +
            "        → Both connect to a server within the **local network**.\n" +
            "\n" +
            "    global, global_1\n" +
            "        → Both connect to the **first global server**.\n" +
            "\n" +
            "    global_2\n" +
            "        → Connects to the **second global server**.\n" +
            "\n" +
            "Examples:\n" +
            "    connect database-server\n" +
            "    connect database-server localhost\n" +
            "    connect database-server local\n" +
            "    connect database-server local_network\n" +
            "    connect database-server global\n" +
            "    connect database-server global_1\n" +
            "    connect database-server global_2\n" +
            "    disconnect database-server\n" +
            "───────────────────────────────────────────────\n" +
            "⚙\uFE0F  PIPELINING & FILTERING\n" +
            "───────────────────────────────────────────────\n" +
            "The CLI supports **command chaining** using `|` (pipe symbol).\n" +
            "Multiple commands connected with `|` share the same output buffer.\n" +
            "\n" +
            "This means that the **output** of the first command becomes the\n" +
            "**input** of the next command.\n" +
            "\n" +
            "Examples:\n" +
            "    list -l | grep Python\n" +
            "    list -l | blocks with AI\n" +
            "    search title \"Machine\" | grep author\n" +
            "    list -l | blocks with Journal | grep 2020\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDD0E  GREP\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    grep <text>\n" +
            "\n" +
            "Description:\n" +
            "    Filters the current process output and keeps only the lines\n" +
            "    that contain the specified text (case-insensitive).\n" +
            "\n" +
            "Example:\n" +
            "    list -l | grep Python\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83D\uDCE6  BLOCKS WITH\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    blocks with <text>\n" +
            "\n" +
            "Description:\n" +
            "    Filters complete message blocks that contain a specific\n" +
            "    text anywhere in the block.\n" +
            "\n" +
            "This is useful for filtering grouped output (like detailed\n" +
            "book entries) instead of individual lines.\n" +
            "\n" +
            "Examples:\n" +
            "    list -l | blocks with AI\n" +
            "    search title \"Data\" | blocks with available\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "❓  HELP\n" +
            "───────────────────────────────────────────────\n" +
            "Command:\n" +
            "    ?\n" +
            "\n" +
            "Description:\n" +
            "    Displays this help overview.\n" +
            "\n" +
            "───────────────────────────────────────────────\n" +
            "\uD83E\uDDF0  SUMMARY\n" +
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
            "You can combine commands using `|` to chain filters\n" +
            "and view results dynamically.\n" +
            "───────────────────────────────────────────────\n";
}
