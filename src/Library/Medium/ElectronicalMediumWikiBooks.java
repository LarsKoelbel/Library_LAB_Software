package Library.Medium;

import Library.bib_tex.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * This class is used to represent an electronic storage medium from WikiBooks
 * @author lkoelbel
 * @matnr 21487
 */
public class ElectronicalMediumWikiBooks extends ElectronicalMedium implements Serializable {
    private String contributor;
    private String tableOfContents;
    private LocalDateTime timestamp;
    private String shelf;

    public ElectronicalMediumWikiBooks()
    {
        setType(BibTexType.EL_MED_WIKI);
        setDataFormat("WikiBooks book");
        setStatus(Status.AVAILABLE);
    }

    public String getContributor() {
        return contributor;
    }

    public ElectronicalMediumWikiBooks setContributor(String contributor) {
        this.contributor = contributor;
        return this;
    }

    public String getTableOfContents() {
        return tableOfContents;
    }

    public ElectronicalMediumWikiBooks setTableOfContents(String tableOfContents) {
        this.tableOfContents = tableOfContents;
        return this;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public ElectronicalMediumWikiBooks setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getShelf() {
        return shelf;
    }

    public ElectronicalMediumWikiBooks setShelf(String shelf) {
        this.shelf = shelf;
        return this;
    }

    @Override
    public String generateRepresentation() {
        StringBuilder sb = new StringBuilder(super.generateRepresentation()).append("\n");
        sb.append("Contributor: ").append(getContributor()).append("\n");
        sb.append("Timestamp: ").append(getTimestamp()).append("\n");
        sb.append("Shelf: ").append(getShelf()).append("\n");
        sb.append("Table of contents: ").append("\n").append(getTableOfContents());
        return sb.toString();
    }
}
