package Library.xml;

public class XMLNullNode extends XMLNode{
    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public XMLNode get(String _qName) {
        return this;
    }

    @Override
    public String getContent() {
        return null;
    }
}
