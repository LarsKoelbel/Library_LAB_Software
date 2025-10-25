package Library.user_interface;

import java.util.HashMap;
import java.util.Map;

/**
* Class representing a Node in a tree
* @author lkoeble 21487
*/
public class Node <T, C>{
    private Map<T, Node<T, C>> outMap = new HashMap<>();

    private C content = null;

    /**
     * Proceed to the next node based on a key in the out map
     * @param _key The key
     * @return The next node or null
     */
    public Node<T,C> proceed(T _key)
    {
        if (outMap.containsKey(_key)) return outMap.get(_key);
        return null;
    }

    /**
     * Proceed to the next node based on a key in the out map or create it, if it doesn't exist
     * @param key The key
     * @return The next node
     */
    public Node<T,C> proceedOrCreate(T key)
    {
        Node<T, C> next = proceed(key);
        if (next == null)
        {
            Node<T, C> node = new Node<>();
            outMap.put(key, node);
            return node;
        }
        return next;
    }

    /**
     * Get the content of the
     * @return Content
     */
    public C get()
    {
        return content;
    }

    /**
     * Set the content of the node
     * @param _content New content
     * @return self
     */
    public Node<T, C> set(C _content)
    {
        content = _content;
        return this;
    }

    /**
     * Check if the node has content
     * @return true or fals
     */
    public boolean hasContent()
    {
        return content != null;
    }
}
