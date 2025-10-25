package Library;

import Library.Medium.Medium;
import Library.bib_tex.BibTexParser;
import Library.io.*;

/**
 * Main class for managing the library
 * @author lkoelbel
 * @matnr 21487
 */
public class Library {
    public static void main(String[] argv)
    {
        Collection collection = new Collection();

        ProcessOutputBuffer out = new ProcessOutputBuffer("collection-test");

        collection.addMedium("@book{author = {Alice Weber}, title = {Data Structures and Algorithms}, publisher = {TechPress, Berlin}, year = 2015, isbn = {978-3-16-148410-0}, pages = 520, edition = {first}}", out);
        collection.addMedium("@book{author = {John Schmidt}, title = {Introduction to Machine Learning}, publisher = {ML Books, Munich}, year = 2019, isbn = {978-3-16-148410-0}, pages = 410, edition = {second}}", out);
        collection.addMedium("@book{author = {Maria Fischer}, title = {Deep Learning with Python}, publisher = {AI Publications, Hamburg}, year = 2021, isbn = {978-3-16-148410-0}, pages = 350, edition = {first}}", out);
        collection.addMedium("@book{author = {Peter Neumann}, title = {Operating Systems Explained}, publisher = {CompSci Verlag, Frankfurt}, year = 2017, isbn = {978-3-16-148410-0}, pages = 600, edition = {third}}", out);
        collection.addMedium("@book{author = {Clara Bauer}, title = {Database Systems}, publisher = {Data Press, Cologne}, year = 2020, isbn = {978-3-16-148410-0}, pages = 480, edition = {second}}", out);


        collection.addMedium("@journal{title = {Journal of AI Research}, issn = {1076-9757}, volume = 58, number = 12, pages = 200, edition = {first}}", out);
        collection.addMedium("@journal{title = {Science Advances}, issn = {2375-2548}, volume = 36, number = 9, pages = 180, edition = {second}}", out);
        collection.addMedium("@journal{title = {Nature Neuroscience}, issn = {1097-6256}, volume = 23, number = 7, pages = 210, edition = {first}}", out);
        collection.addMedium("@journal{title = {ACM Computing Surveys}, issn = {0360-0300}, volume = 50, number = 4, pages = 320, edition = {third}}", out);
        collection.addMedium("@journal{title = {IEEE Transactions on Robotics}, issn = {1552-3098}, volume = 37, number = 6, pages = 250, edition = {first}}", out);

        collection.addMedium("@cd{title = {Back in Black}, artist = {AC/DC}, label = {Albert Productions}, duration = 41.5, agePolicy = {all ages}}", out);
        collection.addMedium("@cd{title = {Rumours}, artist = {Fleetwood Mac}, label = {Warner Bros.}, duration = 39.3, agePolicy = {all ages}}", out);
        collection.addMedium("@cd{title = {Hotel California}, artist = {Eagles}, label = {Asylum Records}, duration = 43.2, agePolicy = {all ages}}", out);
        collection.addMedium("@cd{title = {The Dark Side of the Moon}, artist = {Pink Floyd}, label = {Harvest Records}, duration = 42.7, agePolicy = {all ages}}", out);
        collection.addMedium("@cd{title = {Led Zeppelin IV}, artist = {Led Zeppelin}, label = {Atlantic Records}, duration = 44.1, agePolicy = {all ages}}", out);

        collection.addMedium("@elMed{title = {Coursera}, URL = {https://www.coursera.org}, dataformat = {HTML Website}, size = 5120}", out);
        collection.addMedium("@elMed{title = {edX}, URL = {https://www.edx.org}, dataformat = {HTML Website}, size = 4096}", out);
        collection.addMedium("@elMed{title = {Stanford Online}, URL = {https://online.stanford.edu}, dataformat = {HTML Website}, size = 3072}", out);
        collection.addMedium("@elMed{title = {MIT OCW}, URL = {https://ocw.mit.edu}, dataformat = {HTML Website}, size = 2048}", out);
        collection.addMedium("@elMed{title = {Khan Academy}, URL = {https://www.khanacademy.org}, dataformat = {HTML Website}, size = 1024}", out);

        System.out.println(out);

        for (Medium m :collection)
        {
            System.out.println(m.generateShortRepresentation());
        }

        out = new ProcessOutputBuffer("test-drop");

        collection.dropMedium("Rumours", out);
        collection.dropMedium("MIT OCW", out);
        collection.dropMedium("Maria Fischer", out);
        collection.dropMedium("Hotel California", out);

        System.out.println(out);

        out = new ProcessOutputBuffer("test-add-existing");

        collection.addMedium("@journal{title = {Journal of AI Research}, issn = {1076-9757}, volume = 58, number = 12, pages = 200, edition = {first}}", out);

        System.out.println(out);

        out = new ProcessOutputBuffer("test-id-rotation");

        collection.addMedium("@journal{title = {New}, issn = {1076-9757}, volume = 58, number = 12, pages = 200, edition = {first}}", out);
        collection.addMedium("@journal{title = {New2}, issn = {1076-9757}, volume = 58, number = 12, pages = 200, edition = {second}}", out);
        collection.addMedium("@journal{title = {New3}, issn = {1076-9757}, volume = 58, number = 12, pages = 200, edition = {second}}", out);
        collection.addMedium("@journal{title = {New4}, issn = {1076-9757}, volume = 58, number = 12, pages = 200, edition = {second}}", out);
        System.out.println(out);

        for (Medium m :collection)
        {
            System.out.println(m.generateShortRepresentation());
        }

    }

}
