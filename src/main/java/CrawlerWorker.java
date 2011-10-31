import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrawlerWorker implements Runnable {

    static final List<String> watchedPageElements = Arrays.asList("img", "script", "iframe");

    UrlQueue queue;
    ResultsWriter resultsWriter;
    PageFetcher fetcher;
    BugsList bugsList;

    public CrawlerWorker(UrlQueue queue, ResultsWriter resultsWriter, PageFetcher fetcher, BugsList bugsList) {
        this.queue = queue;
        this.resultsWriter = resultsWriter;
        this.fetcher = fetcher;
        this.bugsList = bugsList;
    }

    public void run() {
        String url = null;
        while((url = queue.getNextUrl()) != null) {
            crawlSite(url);
        }
    }

    void crawlSite(String domain) {

        try {
            Document html = fetcher.getHtml("http://" + domain);

            List<Element> watchedElements = new ArrayList<Element>();

            for(String tagName : watchedPageElements) {
                List<Element> els = html.getElementsByTag(tagName);
                watchedElements.addAll(els);
            }

            ArrayList<String> bugsFound = new ArrayList<String>();
            for(Element el : watchedElements) {
                String source = el.attr("src");
                if(bugsList.hasMatchingPatterns(source)) {
                    bugsFound.add(bugsList.getBugNameForString(source));
                }
            }
            resultsWriter.registerSuccessfulCrawl(domain, bugsFound);
        } catch (IOException e) {
            queue.returnUrl(domain);
        }

    }
}
