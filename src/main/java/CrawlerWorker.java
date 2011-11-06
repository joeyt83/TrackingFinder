import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrawlerWorker implements Runnable {

    static final List<String> watchedPageElements = Arrays.asList("img", "script", "iframe", "embed", "object");

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
        Website site;
        while ((site = queue.getNextSite()) != null) {
            crawlSite(site);
        }
    }

    void crawlSite(Website site) {

        Document html = getAndParseDocument(site);
        if(html == null) {
            handleFailure(site);
            return;
        }

        List<Element> watchedElements = collectAllWatchedPageElements(html);
        ArrayList<String> bugsFound = findBugsFromWatchedElements(watchedElements);
        resultsWriter.registerSuccessfulCrawl(site, bugsFound);

    }

    private ArrayList<String> findBugsFromWatchedElements(List<Element> watchedElements) {
        ArrayList<String> bugsFound = new ArrayList<String>();
        for (Element el : watchedElements) {
            String source = el.attr("src");
            boolean isLocalImage = (el.tagName() == "img" && !source.startsWith("http"));
            if (source != "" && !isLocalImage) {
                if (bugsList.hasMatchingPatterns(source)) {
                    String bugName = bugsList.getBugNameForString(source);
                    if (!(bugsFound.contains(bugName))) {
                        bugsFound.add(bugName);
                    }
                }
            }
        }
        return bugsFound;
    }

    private List<Element> collectAllWatchedPageElements(Document html) {
        List<Element> watchedElements = new ArrayList<Element>();
        for (String tagName : watchedPageElements) {
            List<Element> els = html.getElementsByTag(tagName);
            watchedElements.addAll(els);
        }
        return watchedElements;
    }

    private void handleFailure(Website site) {
        if(++site.failCount >= 5) {
            resultsWriter.registerFailedCrawl(site);
        } else {
            queue.returnFailedSiteToQueue(site);
        }
    }

    private Document getAndParseDocument(Website site) {
        Document html = null;
        try {
            html = fetcher.getHtml("http://www." + site.domain);
        } catch (Exception e) {
//            System.out.println(e.getMessage());
        }

        if (html == null) {
            try {
                html = fetcher.getHtml("http://" + site.domain);
            } catch (Exception e) {
//                System.out.println(e.getMessage());
            }
        }
        return html;
    }
}
