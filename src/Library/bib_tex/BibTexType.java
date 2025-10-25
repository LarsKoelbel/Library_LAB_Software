package Library.bib_tex;

import java.io.Serializable;

/**
 * Type of bibtex (e.g. book or journal)
 * @author lkoelbel 21487
 */
public enum BibTexType implements Serializable {
    BOOK("book",0),
    JOURNAL("journal",1),
    CD("cd",2),
    EL_MED("elMed",3),
    MEDIUM("-",4);

    private String regex;
    private int sortLevel;

    /**
     * BibTex type with a regex
     * @param _regex Regex
     * @param _sortLevel Level when sorting
     */
    BibTexType(String _regex, int _sortLevel)
    {
        regex = _regex;
        sortLevel = _sortLevel;
    }

    public String getRegex() {
        return regex;
    }

    public int getSortLevel() {
        return sortLevel;
    }
}
