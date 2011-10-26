import org.junit.Test;

import java.io.File;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class BugsListTests {

    @Test
    public void testBugListLoadedFromBugFile() {
        BugsList list = new BugsList("src/main/resources/bugs.csv");

        assertEquals(743, list.bugPatterns.size());

        for(Pattern p : list.bugPatterns.keySet()) {
            System.out.println(p.matcher("www.google-analytics.com/siteopt.js").matches());
        }

    }
}
