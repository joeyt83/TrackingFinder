import org.junit.Test;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertSame;

public class BugsListTests {

    BugsList list = new BugsList("src/main/resources/bugs.csv");

    @Test
    public void testBugListLoadedFromBugFile() {

        assertEquals(743, list.bugPatterns.size());

    }
    
    @Test
    public void testMotherPatternMatchesForKnownTrackingUrls() {

        List<String> urls = Arrays.asList(
                "hello",
                "hell"
        );

        for (String url : urls) {
            assertTrue(list.motherPattern.matcher(url).matches());
        }

    }

}
