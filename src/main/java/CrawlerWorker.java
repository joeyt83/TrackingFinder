import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
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
            if(++site.failCount >= 5) {
                resultsWriter.registerFailedCrawl(site);
            } else {
                queue.returnFailedSiteToQueue(site);
            }
            return;
        }

        List<Element> watchedElements = new ArrayList<Element>();
        for (String tagName : watchedPageElements) {
            List<Element> els = html.getElementsByTag(tagName);
            watchedElements.addAll(els);
        }

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

        resultsWriter.registerSuccessfulCrawl(site, bugsFound);

    }

    private Document getAndParseDocument(Website site) {
        Document html = null;
        try {
            html = fetcher.getHtml("http://www." + site.domain);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (html == null) {
            try {
                html = fetcher.getHtml("http://" + site.domain);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return html;
    }
}
