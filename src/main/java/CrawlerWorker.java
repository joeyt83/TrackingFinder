import java.util.Arrays;
import java.util.List;

public class CrawlerWorker implements Runnable {

    static final List<String> watchedPageElements = Arrays.asList("img", "script", "iframe");

    Orchestrator orchestrator;
    BugsList bugsList;

    public void run() {

    }

    void crawlSite(String url) {

    }
}
