package Library.utils;

import java.net.URL;

/**
 * This class is used to handle URLs
 * @author lkoelbel
 * @matnr 21487
 */
public class URLUtils {

    /**
     * Exception subclass for URL errors
     */
    public static class URLException extends Exception
    {
        public URLException(String message) {
            super(message);
        }
    }

    /**
     * Check if an URL is valide
     * @param _url URL to check
     * @return True if the URL is valide, otherwise False
     */
    public static boolean checkURLString(String _url)
    {
        return checkURL(_url);
    }


    private static boolean checkURL(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception exception)
        {
            return false;
        }
    }
}
