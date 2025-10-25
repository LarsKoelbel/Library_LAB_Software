package Library.Medium;

/**
 * This class is used as a template for all storage mediums
 * @author lkoelbel
 * @matnr 21487
 */
abstract public class Medium {
    private String title = null;

    public String getTitle() {
        return title;
    }

    public Medium setTitle(String _title) {
        this.title = _title;
        return this;
    }

    /**
     * Methode is used to get a string representation of the storage medium
     * @return String representation
     */
    abstract public String generateRepresentation();


}
