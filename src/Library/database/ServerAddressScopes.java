package Library.database;

/**
* Enum for different data bank servers
* @author lkoeble 21487
*/
public enum ServerAddressScopes {
    LOCAL_HOST("http://localhost:5000/library/", "localhost", true),
    LOCAL_NETWORK("http://192.168.178.159/library/", "local network", true),
    GLOBAL_1("http://ypdpbec5rmupizpk.myfritz.net/library/", "global (internet) 1", true),
    GLOBAL_2("http://ypypbec5rmupitzpk:5000", "global (internet) 2", false),
    DEFAULT(LOCAL_NETWORK);

    public final String url;
    public final String name;
    public final boolean searchOnStartup;

    /**
     * Constructor for creating a new ServerAddressScope
     * @param _url Url of the server in the scope
     * @param _name Name to be displayed to the user
     * @param _searchOnStartup Search for server when starting the program
     */
    ServerAddressScopes(String _url, String _name, boolean _searchOnStartup)
    {
        url = _url;
        name = _name;
        searchOnStartup = _searchOnStartup;
    }

    /**
     * Create a copy of a Server address scope
     * @param _serverAddressScopes Other scope
     */
    ServerAddressScopes(ServerAddressScopes _serverAddressScopes)
    {
        url = _serverAddressScopes.getUrl();
        name = "default";
        searchOnStartup = _serverAddressScopes.searchOnStartup;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public boolean isSearchOnStartup() {
        return searchOnStartup;
    }
}
