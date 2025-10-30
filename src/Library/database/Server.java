package Library.database;

import Library.Collection;
import Library.Medium.Medium;
import Library.Medium.Status;
import Library.io.ProcessOutputBuffer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;

import Library.io.Severity;
import org.json.JSONObject;

/**
* Class representing the database server
* @author lkoeble 21487
*/
public class Server {
    private  String SERVER_URL = null;
    private String USERNAME = "alice";
    private String PASSWORD = "database";

    /**
     * Create a new server (connection) with a username and a password
     * @param _username Username
     * @param _password Password
     * @param _url URL of the server
     */
    public Server(String _username, String _password, String _url)
    {
        USERNAME = _username;
        PASSWORD = _password;
        SERVER_URL = _url;
    }

    // Helper function to send POST requests with JSON
    private JSONObject postJSON(String endpoint, JSONObject payload) throws Exception {
        URL url = new URL(SERVER_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        // Set timeouts (milliseconds)
        conn.setConnectTimeout(5000);  // 5 seconds to connect
        conn.setReadTimeout(10000);    // 10 seconds to read response

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        InputStream is = conn.getResponseCode() < 400
                ? conn.getInputStream()
                : conn.getErrorStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line.trim());
        }

        conn.disconnect();
        return new JSONObject(response.toString());
    }

    // Helper to compute MD5 digest as base64 or hex string
    private String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));  // matches Python .hexdigest()
        }

        return sb.toString();
    }

    /**
     * Authenticate with the server
     * @return Auth hash if success else null
     */
    private String authenticate() throws Exception {
        // STEP 1: Start authentication
        JSONObject startPayload = new JSONObject();
        startPayload.put("username", USERNAME);
        JSONObject startResponse = postJSON("/auth/start", startPayload);

        if (!startResponse.has("challenge")) {
            throw new DatabaseException("Auth start failed", "Authentification with the server failed due to an unexpected response.");
        }

        int challenge = startResponse.getInt("challenge");

        // STEP 2: Compute response
        // Hash string format: {username}::{REMOTE_PASSWORD}::<secret>{challenge}
        String input = USERNAME + "::" + PASSWORD + "::" + "<secret>" + challenge;
        String authDigest = md5(input);

        JSONObject verifyPayload = new JSONObject();
        verifyPayload.put("username", USERNAME);
        verifyPayload.put("response", authDigest);
        JSONObject verifyResponse = postJSON("/auth/verify", verifyPayload);

        if (!"ok".equals(verifyResponse.optString("status"))) {
            System.err.println("Auth verify failed: " + verifyResponse);
            throw new DatabaseException("Auth failed", "Authentification with the server failed: Server denied access. Check username and password!");
        }

        return authDigest;
    }

    /**
     * Drop authentification with server
     * @param _authHash Auth hash
     * @throws Exception in case of errors
     */
    private void drop(String _authHash) throws Exception {
        // STEP 4: Drop connection
        JSONObject dropPayload = new JSONObject();
        dropPayload.put("username", USERNAME);
        dropPayload.put("auth", _authHash);
        JSONObject dropResponse = postJSON("/drop", dropPayload);
    }

    private String getData() throws Exception {

            String authDigest = authenticate();

            // STEP 3: Request data
            JSONObject dataPayload = new JSONObject();
            dataPayload.put("username", USERNAME);
            dataPayload.put("auth", authDigest);
            JSONObject dataResponse = postJSON("/get/data/all", dataPayload);

            drop(authDigest);

            return dataResponse.getString("payload");
    }

    /**
     * Get an available id from the server for adding a new medium to the database
     * @param _out Process output buffer
     * @return The id or -1
     */
    public long getFreeId(ProcessOutputBuffer _out)
    {
        try {
            String authDigest = authenticate();

            // STEP 3: Request data
            JSONObject dataPayload = new JSONObject();
            dataPayload.put("username", USERNAME);
            dataPayload.put("auth", authDigest);
            JSONObject dataResponse = postJSON("/get/free-id", dataPayload);

            drop(authDigest);

            return dataResponse.getLong("id");
        }
        catch (Exception e)
        {
            if (e instanceof ConnectException)
            {
                _out.write("Can't connect to server. Check internet connection or contact administrator", Severity.ERROR);
            } else if (e instanceof DatabaseException)
            {
                _out.write(((DatabaseException) e).getUserMessage(), Severity.ERROR);
            } else
            {
                _out.write("Possibly fatale error during server communication. Please contact administrator: " + e.getClass() + " :: " + e.getMessage(), Severity.FATAL);
            }

            return -1;

        }
    }

    /**
     * Add a new medium to the database
     * @param _medium The medium
     * @param _out Process output buffer
     * @return Status
     */
    public boolean add(Medium _medium, ProcessOutputBuffer _out)
    {
        try {

            String data = Collection.getDataBaseString(_medium);

            String authDigest = authenticate();

            // STEP 3: Request data
            JSONObject dataPayload = new JSONObject();
            dataPayload.put("username", USERNAME);
            dataPayload.put("auth", authDigest);
            dataPayload.put("data", data);
            JSONObject dataResponse = postJSON("/add", dataPayload);

            drop(authDigest);

            if (dataResponse.getString("status").equalsIgnoreCase("ok")) return true;
            else {
                _out.write("Adding object to database failed. Message from server: " + dataPayload.getString("message"), Severity.ERROR);
                return false;
            }
        }
        catch (Exception e)
        {
            if (e instanceof ConnectException)
            {
                _out.write("Can't connect to server. Check internet connection or contact administrator", Severity.ERROR);
            } else if (e instanceof DatabaseException)
            {
                _out.write(((DatabaseException) e).getUserMessage(), Severity.ERROR);
            } else
            {
                _out.write("Possibly fatale error during server communication. Please contact administrator: " + e.getClass() + " :: " + e.getMessage(), Severity.FATAL);
            }

            return false;

        }
    }

    /**
     * Delete a medium from the database
     * @param _medium The medium
     * @param _out Process output buffer
     * @return Status
     */
    public boolean delete(Medium _medium, ProcessOutputBuffer _out)
    {
        try {

            String data = Collection.getDataBaseString(_medium);

            String authDigest = authenticate();

            // STEP 3: Request data
            JSONObject dataPayload = new JSONObject();
            dataPayload.put("username", USERNAME);
            dataPayload.put("auth", authDigest);
            dataPayload.put("id", String.valueOf(_medium.getInventoryID()));
            JSONObject dataResponse = postJSON("/delete", dataPayload);

            drop(authDigest);

            if (dataResponse.getString("status").equalsIgnoreCase("ok")) return true;
            else {
                _out.write("Dropping object from database failed. Message from server: " + dataPayload.getString("message"), Severity.ERROR);
                return false;
            }
        }
        catch (Exception e)
        {
            if (e instanceof ConnectException)
            {
                _out.write("Can't connect to server. Check internet connection or contact administrator", Severity.ERROR);
            } else if (e instanceof DatabaseException)
            {
                _out.write(((DatabaseException) e).getUserMessage(), Severity.ERROR);
            } else
            {
                _out.write("Possibly fatale error during server communication. Please contact administrator: " + e.getClass() + " :: " + e.getMessage(), Severity.FATAL);
            }

            return false;

        }
    }

    /**
     * Modify the status of a medium
     * @param _medium The medium
     * @param _out Process output buffer
     * @param _status Status to be set
     * @return Status
     */
    public boolean modStatus(Medium _medium, ProcessOutputBuffer _out, Status _status)
    {
        try {

            String id = String.valueOf(_medium.getInventoryID());
            String statusString = "-1";

            switch (_status)
            {
                case Status.AVAILABLE -> statusString = "1";
                case Status.CHECKED_OUT -> statusString = "0";
            }

            String authDigest = authenticate();

            // STEP 3: Request data
            JSONObject dataPayload = new JSONObject();
            dataPayload.put("username", USERNAME);
            dataPayload.put("auth", authDigest);
            dataPayload.put("id", id);
            dataPayload.put("status", statusString);
            JSONObject dataResponse = postJSON("/mod-status", dataPayload);

            drop(authDigest);

            if (dataResponse.getString("status").equalsIgnoreCase("ok")) return true;
            else {
                _out.write("Changing status failed on server side. Message from server: " + dataPayload.getString("message"), Severity.ERROR);
                return false;
            }
        }
        catch (Exception e)
        {
            if (e instanceof ConnectException)
            {
                _out.write("Can't connect to server. Check internet connection or contact administrator", Severity.ERROR);
            } else if (e instanceof DatabaseException)
            {
                _out.write(((DatabaseException) e).getUserMessage(), Severity.ERROR);
            } else
            {
                _out.write("Possibly fatale error during server communication. Please contact administrator: " + e.getClass() + " :: " + e.getMessage(), Severity.FATAL);
            }

            return false;

        }
    }

    /**
     * Get the collection of the server database
     * @param _out Output buffer
     * @return The collection or null
     */
    public Collection getCollectionFromDatabase(ProcessOutputBuffer _out)
    {
        try {
            String data = getData();
            if (data == null) {
                _out.write("Unknown error while trying to load data from server", Severity.ERROR);
                return null;
            }

            Collection collection = Collection.fromDataBaseString(data);
            _out.write("Elements collected from server: " + collection.length(), Severity.SUCCESS);
            return Collection.fromDataBaseString(data);
        }
        catch (Exception e)
        {
            if (e instanceof ConnectException)
            {
                _out.write("Can't connect to server. Check internet connection or contact administrator", Severity.ERROR);
            } else if (e instanceof DatabaseException)
            {
                _out.write(((DatabaseException) e).getUserMessage(), Severity.ERROR);
            } else
            {
                _out.write("Possibly fatale error during server communication. Please contact administrator: " + e.getClass() + " :: " + e.getMessage(), Severity.FATAL);
            }

            return null;

        }
    }

    /**
     * Test the connection to the server
     * @return Status of server connection
     */
    public boolean testConnection()
    {
        try
        {
            JSONObject dataPayload = new JSONObject();
            dataPayload.put("username", USERNAME);
            JSONObject dataResponse = postJSON("/test", dataPayload);

            if (dataResponse.getString("status").equalsIgnoreCase("ok")) return true;
            return false;
        }catch (Exception e){
            return false;
        }

    }

    /**
     * Test authentication with the server
     * @param _out Process output buffer
     * @return Status
     */
    public boolean testAuth(ProcessOutputBuffer _out)
    {
        try {
            if (testConnection()) drop(authenticate());
            else{
                _out.write("Can't connect to server. Check internet connection or contact administrator", Severity.ERROR);
                return false;
            }
            return true;
        }
        catch (Exception e)
        {
            if (e instanceof ConnectException)
            {
                _out.write("Cant connect to server. Chack internet connection or contact administrator", Severity.ERROR);
            } else if (e instanceof DatabaseException)
            {
                _out.write(((DatabaseException) e).getUserMessage(), Severity.ERROR);
            } else
            {
                _out.write("Possibly fatale error during server communication. Please contact administrator: " + e.getClass() + " :: " + e.getMessage(), Severity.FATAL);
            }

            return false;

        }
    }
}
