package Library.xml;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class represents a (simple) xml node
 * @author lkoeble 21487
 */
public class XMLNode implements Iterable<XMLNode> {
    private String qName;
    private String content;
    private ArrayList<XMLNode> children = new ArrayList<>();

    public String getqName() {
        return qName;
    }

    public void setqName(String qName) {
        this.qName = qName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<XMLNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<XMLNode> children) {
        this.children = children;
    }

    /**
     * Get the first child with a specific qName
     * @param _qName qName of the child to get
     * @return The child or nullNode
     */
    public XMLNode get(String _qName)
    {
        for (XMLNode node : this)
        {
            if (node.getqName().equals(_qName)) return node;
        }
        return new XMLNullNode();
    }

    /**
     * Check weather the node has a child with a specific qName
     * @param _qName qName of the child to check
     * @return True is the child exists
     */
    public boolean has(String _qName)
    {
        return !get(_qName).isNull();
    }

    /**
     * Add a child to the node
     * @param _node The child node
     */
    public void addChild(XMLNode _node)
    {
        this.children.add(_node);
    }

    @Override
    public Iterator<XMLNode> iterator() {
        return new XMLNodeIterator();
    }

    /**
     * Iterator for the xml children
     * @author lkoeble 21487
     */
    public class XMLNodeIterator implements Iterator<XMLNode>{

        private int pos = 0;

        @Override
        public boolean hasNext() {
            return pos < (children.size());
        }

        @Override
        public XMLNode next() {
            return children.get(pos++);
        }
    }

    @Override
    public String toString() {
        return qName + " :: " + content;
    }

    /**
     * Get a string representation of the node
     * @param indent child level of the node
     * @return The string
     */
    public String repr(int indent)
    {
        StringBuilder sb = new StringBuilder();
        if (indent > 0) sb.append("\t".repeat(indent));
        sb.append(this);
        for (XMLNode child : this)
        {
            sb.append("\n").append(child.repr(indent + 1));
        }
        return sb.toString();
    }

    /**
     * Check weather the node is null in the struct
     * @return True if the node is null
     */
    public boolean isNull()
    {
        return false;
    }
}
