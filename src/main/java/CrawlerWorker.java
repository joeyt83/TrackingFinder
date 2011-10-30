import java.util.Arrays;
import java.util.List;

public class CrawlerWorker implements Runnable {

    static final List<String> watchedPageElements = Arrays.asList("img", "script", "iframe");

    Orchestrator orchestrator;
    PageFetcher fetcher;
    BugsList bugsList;

    public void run() {

    }

    void crawlSite(String domain) {

        String html = fetcher.getHtml("http://" + domain);
     
    }
}
