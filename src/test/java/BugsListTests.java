import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;

public class BugsListTests {

    BugsList list = new BugsList("src/main/resources/bugs.csv");

    @Test
    public void testBugListLoadedFromBugFile() {
        assertEquals(743, list.getnumberOfBugs());
    }
    
    @Test
    public void testMotherPatternMatchesForKnownTrackingUrls() {

        List<String> urls = Arrays.asList(
                "indextools.js",
                "static.scribefire.com/ads.js",
                "hits.convergetrack.com/",
                "webtraxs.js",
                ".adjug.com"
        );

        for (String url : urls) {
            assertTrue(list.hasMatchingPatterns(url));
        }

    }

    @Test
    public void testPartialMatchesAreCounted() {

        List<String> urls = Arrays.asList(
                "indextools.js?asdkajsdkj",
                "static.scribefire.com/ads.js?x=yz",
                "hits.convergetrack.com/something"
        );

        for (String url : urls) {
            assertTrue(list.hasMatchingPatterns(url));
        }

    }

    @Test
    public void testBugPatternsReturnCorrectNameForKnownPattern() {

        assertEquals("WebTrends", list.getBugNameForString("m.webtrends.com"));
        assertEquals("Statisfy", list.getBugNameForString("statisfy.net/javascripts/stats.js"));
        assertEquals("BridgeTrack", list.getBugNameForString(".bridgetrack.com/track"));
        assertEquals("Carbon Ads", list.getBugNameForString(".carbonads.com"));
        assertEquals("AdPlan", list.getBugNameForString("c.p-advg.com"));

        assertNull(list.getBugNameForString("asdkljfhsd/sgfsd.com"));
        assertNull(list.getBugNameForString("x.webtrends.com"));
        assertNull(list.getBugNameForString(""));
        assertNull(list.getBugNameForString("/sgfsd.js"));
    }

}
