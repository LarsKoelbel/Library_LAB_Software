package Library.Medium;

import Library.utils.ISBNUtils;
import Library.bib_tex.BibTexParameter;
import Library.bib_tex.BibTexStruct;
import Library.io.Communication;
import Library.io.Severity;

/**
 * This class is used to represent a paper (newspaper) storage medium
 * @author lkoelbel
 * @matnr 21487
 */
public class Book extends Medium{
    private int yearOfPublishing = 0;
    private String publicher = null;
    private String ISBN = null;
    private String writer = null;

    public int getYearOfPublishing() {
        return yearOfPublishing;
    }

    public Book setYearOfPublishing(int _yearOfPublishing) {
        this.yearOfPublishing = _yearOfPublishing;
        return this;
    }

    public String getPublicher() {
        return publicher;
    }

    public Book setPublicher(String _publicher) {
        this.publicher = _publicher;
        return this;
    }

    public String getISBN() {
        return ISBN;
    }

    public Book setISBN(String _ISBN) {
        if (ISBNUtils.checkISBNString(_ISBN))
        {
            this.ISBN = _ISBN;
        }
        else
        {
            System.out.println(String.format("ISBN %s is not valide. The ISBN was not changed", _ISBN));
        }

        return this;
    }

    public String getWriter() {
        return writer;
    }

    public Book setWriter(String _writer) {
        this.writer = _writer;

        return this;
    }

    /**
     * Methode is used to get a string representation of the storage medium
     * @return String representation
     */
    @Override
    public String generateRepresentation()
    {
        StringBuilder sp = new StringBuilder();
        sp.append("Title: ").append(getTitle()).append("\n");
        sp.append("Year of publishing: ").append(getYearOfPublishing()).append("\n");
        sp.append("Publisher: ").append(getPublicher()).append("\n");
        sp.append("ISBN: ").append(getISBN()).append("\n");
        sp.append("Author: ").append(getWriter());

        return sp.toString();
    }

    /**
     * Parse values from a BibTex struct object
     * @param _bibTexStruct BibTex struct to parse from
     */
    public Book parseFromBibTexStruct(BibTexStruct _bibTexStruct)
    {
        for (BibTexParameter bibTexParameter : _bibTexStruct.getParameterList())
        {
            switch (bibTexParameter.getName().toLowerCase().strip()){
                case "author" -> setWriter(bibTexParameter.getSvalue());
                case "title" -> setTitle(bibTexParameter.getSvalue());
                case "publisher" -> setPublicher(bibTexParameter.getSvalue());
                case "year" -> setYearOfPublishing((int) bibTexParameter.getFvalue());
                case "isbn" -> setISBN(bibTexParameter.getSvalue());
                default -> Communication.writeToProcessOutputBuffer("bib-tex-parser","Parameter " + bibTexParameter.getName() + " is not available for type Book", Severity.WARNING);

            }
        }

        return this;
    }


}
