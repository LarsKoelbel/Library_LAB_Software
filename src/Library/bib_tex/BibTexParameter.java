package Library.bib_tex;

/**
 * Class representing a bibtex parameter inside the bibtex struct
 * @author lkoelbel 21487
 */
public class BibTexParameter {
    private String name = null;
    private double fvalue = 0;
    private String svalue = null;
    private BibTexParameterType type = null;

    public String getName() {
        return name;
    }

    public BibTexParameter setName(String _name) {
        this.name = _name;
        return this;
    }

    public double getFvalue() {
        return fvalue;
    }

    public BibTexParameter setFvalue(Double _fvalue) {
        this.fvalue = _fvalue;
        return this;
    }

    public String getSvalue() {
        return svalue;
    }

    public BibTexParameter setSvalue(String _svalue) {
        this.svalue = _svalue;
        return this;
    }

    public BibTexParameterType getType() {
        return type;
    }

    public BibTexParameter setType(BibTexParameterType _type) {
        this.type = _type;
        return this;
    }

    /**
     * Generate a string representation of the parameter
     * @return String representation of the parameter
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        if (this.type == null) {
            sb.append("Parameter has not jet been initialized");
            return sb.toString();
        }

        sb.append("\tBibTex parameter of type ")
                .append(this.type.toString())
                .append("\n\t")
                .append("Name: ")
                .append(this.name)
                .append("\n\t")
                .append("Numeric Value: ").append(fvalue).append("\n\t")
                .append("String Value: ").append(svalue).append("\n");

        return sb.toString();
    }
}
