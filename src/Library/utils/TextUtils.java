package Library.utils;

/**
* Class for text util functions
* @author lkoeble 21487
*/
public class TextUtils {

    /**
     * Return a test in a specific style
     * @param _s The test
     * @param _style The style (own code or use src/Library/utils/IColorUtils.java)
     * @return The formated text
     */
    public static String style(String _s, String _style)
    {
        return _style + _s + IColorCodes.RESET;
    }
}
