package Library.bib_tex;

import Library.Medium.*;
import Library.io.Communication;

/**
 * Class for parsing a media object from a bib tex string
 * @author lkoelbel 21487
 */
public class BibTexParser {

    /**
     * Parse a new media object from a bib tex string
     * @param _input BibTex string
     * @throws BibTexException if the string cant be parsed. Implements user readable interface for all exceptions.
     * @return The object or null
     */
    public static Medium parseFromBibTexString(String _input) throws BibTexException
    {
        BibTexStruct bibTexStruct = new BibTexStruct().parseFromString(_input);

        Medium medium = null;

        switch (bibTexStruct.getType()){
            case BibTexType.BOOK -> medium = new Book().parseFromBibTexStruct(bibTexStruct);
            case BibTexType.CD -> medium = new CD().parseFromBibTexStruct(bibTexStruct);
            case BibTexType.EL_MED -> medium = new ElectronicalMedium().parseFromBibTexStruct(bibTexStruct);
            case BibTexType.JOURNAL -> medium = new Paper().parseFromBibTexStruct(bibTexStruct);
            default -> Communication.writeToProcessOutputBuffer("bib-tex-parser","The type " + bibTexStruct.getType() + " in not (yet) supported");
        }

        return medium;
    }
}
