package Library.utils;

import java.util.ArrayList;

/**
* Class for text util functions
* @author lkoeble 21487
*/
public abstract class TextUtils {

    /**
     * Return a text in a specific style
     * @param _s The test
     * @param _style The style (own code or use src/Library/utils/IColorUtils.java)
     * @return The formated text
     */
    public static String style(String _s, String _style)
    {
        return _style + _s + IColorCodes.RESET;
    }

    /**
     * Get all substrings (Strings quoted with "..." from a string).
     * @param _string Array of substrings or null
     * @return The array
     */
    public static String[] getSubstrings(String _string)
    {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder sb = null;
        for (int i = 0; i<_string.length(); i++)
        {
            char c = _string.charAt(i);
            if (c == '"')
            {
                if(sb == null)
                {
                    sb = new StringBuilder();
                }
                else
                {
                    result.add(sb.toString());
                    sb = null;
                }
            }
            else
            {
                if(sb != null) sb.append(c);
            }
        }

        if (!result.isEmpty()) return result.toArray(new String[0]);
        return null;
    }
}
