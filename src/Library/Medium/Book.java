package Library.Medium;

import Library.bib_tex.*;
import Library.io.ProcessOutputBuffer;
import Library.utils.ISBNUtils;
import Library.io.Communication;
import Library.io.Severity;

import java.io.Serializable;

/**
 * This class is used to represent a paper (newspaper) storage medium
 * @author lkoelbel
 * @matnr 21487
 */
public class Book extends Medium implements Serializable {
    private int yearOfPublishing = 0;
    private String publicher = null;
    private String ISBN = null;
    private String writer = null;
    // New
    private String edition = null;
    private int pages = -1;

    public Book()
    {
        super.setType(BibTexType.BOOK);
    }


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

    /**
     * Set the ISBN. Checks if the ISBN is valide
     * @param _ISBN ISBN
     * @return The object itself (chained calls)
     * @throws ISBNUtils.ISBNException if the isbn is invalid
     */
    public Book setISBN(String _ISBN) throws ISBNUtils.ISBNException {
        if (ISBNUtils.checkISBNString(_ISBN))
        {
            this.ISBN = _ISBN;
        }
        else
        {
            throw new ISBNUtils.ISBNException(String.format("ISBN %s is not valide.", _ISBN));
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

    public String getEdition() {
        return edition;
    }

    public void setEdition(String _edition) {
        this.edition = _edition;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int _pages) {
        this.pages = _pages;
    }

    /**
     * Methode is used to get a string representation of the storage medium
     * @return String representation
     */
    @Override
    public String generateRepresentation()
    {
        StringBuilder sp = new StringBuilder();
        sp.append("Inventory ID: ").append(getInventoryID()).append("\n");
        sp.append("Title: ").append(getTitle()).append("\n");
        sp.append("Status: ").append(getStatus()).append("\n");
        sp.append("Date of Return: ").append(getDateOfReturn() != null ? getDateOfReturn() : "N/A").append("\n");
        sp.append("Year of publishing: ").append(getYearOfPublishing()).append("\n");
        sp.append("Publisher: ").append(getPublicher()).append("\n");
        sp.append("ISBN: ").append(getISBN()).append("\n");
        sp.append("Author: ").append(getWriter()).append("\n");
        sp.append("Edition: ").append(getEdition()).append("\n");
        sp.append("Pages: ").append(getPages());
        return sp.toString();
    }

    /**
     * Get a bibtex struct of the object
     * @return the BibTexStruct
     */
    @Override
    public BibTexStruct getBibtex()
    {
        BibTexStruct bibTex = new BibTexStruct();
        bibTex.init().setType(BibTexType.BOOK)
                .addParameter(new BibTexParameter()
                        .setName("Author")
                        .setSvalue(getWriter())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("Title")
                        .setSvalue(getTitle())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("Publisher")
                        .setSvalue(getPublicher())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("Year")
                        .setFvalue((double) getYearOfPublishing())
                        .setType(BibTexParameterType.NUMERIC_VALUE))
                .addParameter(new BibTexParameter()
                        .setName("ISBN")
                        .setSvalue(getISBN())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("Edition")
                        .setSvalue(getEdition())
                        .setType(BibTexParameterType.STRING))
                .addParameter(new BibTexParameter()
                        .setName("Pages")
                        .setFvalue((double) getPages())
                        .setType(BibTexParameterType.NUMERIC_VALUE));

        return bibTex;
    }

    /**
     * Parse values from a BibTex struct object
     * @param _bibTexStruct BibTex struct to parse from
     * @param _out Output buffer for the process
     */
    public Book parseFromBibTexStruct(BibTexStruct _bibTexStruct, ProcessOutputBuffer _out)
    {
        try
        {
            for (BibTexParameter bibTexParameter : _bibTexStruct.getParameterList())
            {
                switch (bibTexParameter.getName().toLowerCase().strip()){
                    case "author" -> setWriter(bibTexParameter.getSvalue());
                    case "title" -> setTitle(bibTexParameter.getSvalue());
                    case "publisher" -> setPublicher(bibTexParameter.getSvalue());
                    case "year" -> setYearOfPublishing((int) bibTexParameter.getFvalue());
                    case "isbn" -> setISBN(bibTexParameter.getSvalue());
                    case "edition" -> setEdition(bibTexParameter.getSvalue());
                    case "pages" -> setPages((int) bibTexParameter.getFvalue());
                    default -> _out.write("Parameter " + bibTexParameter.getName() + " is not available for type Book", Severity.WARNING);

                }

            }

            // Check if all the parameters are met
            if (this.writer == null || this.writer.isEmpty())
                throw new BibTexException("Incomplete parameter exception",
                        "Medium can not be created. The parameter 'author' is missing or empty.");

            if (this.getTitle() == null || this.getTitle().isEmpty())
                throw new BibTexException("Incomplete parameter exception",
                        "Medium can not be created. The parameter 'title' is missing or empty.");

            if (this.publicher == null || this.publicher.isEmpty())
                throw new BibTexException("Incomplete parameter exception",
                        "Medium can not be created. The parameter 'publisher' is missing or empty.");

            if (this.yearOfPublishing <= 0)
                throw new BibTexException("Incomplete parameter exception",
                        "Medium can not be created. The parameter 'year' is missing or invalid.");

            if (this.ISBN == null || this.ISBN.isEmpty())
                throw new BibTexException("Incomplete parameter exception",
                        "Medium can not be created. The parameter 'isbn' is missing or empty.");

            if (this.pages < 0)
                throw new BibTexException("Incomplete parameter exception",
                        "Medium can not be created. The parameter 'pages' is missing or empty.");

            if (this.edition == null || this.edition.isEmpty())
                throw new BibTexException("Incomplete parameter exception",
                        "Medium can not be created. The parameter 'edition' is missing or empty.");

            return this;
        }catch (ISBNUtils.ISBNException e)
        {
            throw new BibTexException("ISBN exception", "Medium can not be created. " + e.getMessage());
        }

    }


}
