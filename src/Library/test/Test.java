package Library.test;

import Library.Collection;
import Library.Medium.Medium;
import Library.io.Communication;
import Library.io.ProcessOutputBuffer;
import Library.persistency.BibTexPersistency;
import Library.persistency.BinaryPersistency;
import Library.persistency.HumanReadablePersistency;

import java.util.Arrays;

/**
 * Test class
 * @author lkoelbel 21487
 */
public class Test {
    /**
     * Call all tests
     * @param argv none
     */
    public static void main(String[] argv)
    {
        test_Searching();
    }


    /**
     * Test the collection class
     */
    private static void test_Collection()
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

    /**
     * Test the persistency
     */
    private static void test_Persistency()
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

        // Save and load

        HumanReadablePersistency humanReadablePersistency = new HumanReadablePersistency();

        humanReadablePersistency.save(collection, "./src/Library/data/human-readable.lib");

        BinaryPersistency binaryPersistency = new BinaryPersistency();

        binaryPersistency.save(collection, "./src/Library/data/objects.lib.bin");

        Collection newCollection = binaryPersistency.load("./src/Library/data/objects.lib.bin");

        System.out.println("Loaded data from file");

        for (Medium m :newCollection)
        {
            System.out.println(m.generateShortRepresentation());
        }

        BibTexPersistency bibTexPersistency = new BibTexPersistency();

        bibTexPersistency.save(collection, "./src/Library/data/objects.lib.bibtex");

        newCollection = bibTexPersistency.load("./src/Library/data/objects.lib.bibtex");

        System.out.println("Loaded data from bibtex file");

        for (Medium m :newCollection)
        {
            System.out.println(m.generateShortRepresentation());
        }

    }

    /**
     * Test searching
     */
    private static void test_Searching()
    {
        Collection collection = new Collection();

        ProcessOutputBuffer out = new ProcessOutputBuffer("collection-test");

        // === BOOKS ===
        collection.addMedium("@book{author = {Alice Weber}, title = {Data Structures and Algorithms}, publisher = {TechPress, Berlin}, year = 2015, isbn = {978-3-16-148410-0}, pages = 520, edition = {first}}", out);
        collection.addMedium("@book{author = {John Schmidt}, title = {Introduction to Machine Learning}, publisher = {ML Books, Munich}, year = 2019, isbn = {978-3-16-148410-0}, pages = 410, edition = {second}}", out);
        collection.addMedium("@book{author = {Maria Fischer}, title = {Deep Learning with Python}, publisher = {AI Publications, Hamburg}, year = 2021, isbn = {978-3-16-148410-0}, pages = 350, edition = {first}}", out);
        collection.addMedium("@book{author = {Peter Neumann}, title = {Operating Systems Explained}, publisher = {CompSci Verlag, Frankfurt}, year = 2017, isbn = {978-3-16-148410-0}, pages = 600, edition = {third}}", out);
        collection.addMedium("@book{author = {Clara Bauer}, title = {Database Systems}, publisher = {Data Press, Cologne}, year = 2020, isbn = {978-3-16-148410-0}, pages = 480, edition = {second}}", out);

// Duplicate title “Data Structures and Algorithms” with different type
        collection.addMedium("@journal{title = {Data Structures and Algorithms}, issn = {1111-2222}, volume = 10, number = 2, pages = 120, edition = {first}}", out);
        collection.addMedium("@cd{title = {Data Structures and Algorithms}, artist = {Code Sounds}, label = {Tech Beats}, duration = 60.5, agePolicy = {all ages}}", out);
        collection.addMedium("@elMed{title = {Data Structures and Algorithms}, URL = {https://datastructures.org}, dataformat = {PDF}, size = 2048}", out);

// === JOURNALS ===
        collection.addMedium("@journal{title = {Journal of AI Research}, issn = {1076-9757}, volume = 58, number = 12, pages = 200, edition = {first}}", out);
        collection.addMedium("@journal{title = {Science Advances}, issn = {2375-2548}, volume = 36, number = 9, pages = 180, edition = {second}}", out);
        collection.addMedium("@journal{title = {Nature Neuroscience}, issn = {1097-6256}, volume = 23, number = 7, pages = 210, edition = {first}}", out);
        collection.addMedium("@journal{title = {ACM Computing Surveys}, issn = {0360-0300}, volume = 50, number = 4, pages = 320, edition = {third}}", out);
        collection.addMedium("@journal{title = {IEEE Transactions on Robotics}, issn = {1552-3098}, volume = 37, number = 6, pages = 250, edition = {first}}", out);

// Duplicate title “Science Advances” as CD and EL_MED for sorting test
        collection.addMedium("@cd{title = {Science Advances}, artist = {AI Beats}, label = {Smart Sounds}, duration = 42.0, agePolicy = {all ages}}", out);
        collection.addMedium("@elMed{title = {Science Advances}, URL = {https://scienceadvances.org}, dataformat = {HTML Website}, size = 8192}", out);

// === CDs ===
        collection.addMedium("@cd{title = {Back in Black}, artist = {AC/DC}, label = {Albert Productions}, duration = 41.5, agePolicy = {all ages}}", out);
        collection.addMedium("@cd{title = {Rumours}, artist = {Fleetwood Mac}, label = {Warner Bros.}, duration = 39.3, agePolicy = {all ages}}", out);
        collection.addMedium("@cd{title = {Hotel California}, artist = {Eagles}, label = {Asylum Records}, duration = 43.2, agePolicy = {all ages}}", out);
        collection.addMedium("@cd{title = {The Dark Side of the Moon}, artist = {Pink Floyd}, label = {Harvest Records}, duration = 42.7, agePolicy = {all ages}}", out);
        collection.addMedium("@cd{title = {Led Zeppelin IV}, artist = {Led Zeppelin}, label = {Atlantic Records}, duration = 44.1, agePolicy = {all ages}}", out);

// === ELECTRONIC MEDIA ===
        collection.addMedium("@elMed{title = {Coursera}, URL = {https://www.coursera.org}, dataformat = {HTML Website}, size = 5120}", out);
        collection.addMedium("@elMed{title = {Coursera}, URL = {https://www.coursera.org/advanced}, dataformat = {HTML Website}, size = 4096}", out);
        collection.addMedium("@elMed{title = {edX}, URL = {https://www.edx.org}, dataformat = {HTML Website}, size = 4096}", out);
        collection.addMedium("@elMed{title = {Stanford Online}, URL = {https://online.stanford.edu}, dataformat = {HTML Website}, size = 3072}", out);
        collection.addMedium("@elMed{title = {MIT OCW}, URL = {https://ocw.mit.edu}, dataformat = {HTML Website}, size = 2048}", out);
        collection.addMedium("@elMed{title = {Khan Academy}, URL = {https://www.khanacademy.org}, dataformat = {HTML Website}, size = 1024}", out);


        System.out.println(out);

        System.out.println(collection.dropMedium("Data Structures and Algorithms", Communication.NULL_BUFFER));

    }
}
