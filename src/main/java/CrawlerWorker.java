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
        String url = null;
        while ((url = queue.getNextUrl()) != null) {
            crawlSite(url);
        }
        System.out.println("Thread finishing " + Thread.currentThread().getName());

    }

    void crawlSite(String domain) {

        Document html = null;

//        long time1 = System.currentTimeMillis();

        try {
            html = fetcher.getHtml("http://www." + domain);
        } catch (IOException e) {
        }

        if (html == null) {
            try {
                html = fetcher.getHtml("http://" + domain);
            } catch (IOException e) {
                resultsWriter.registerFailedCrawl(domain);
                return;
            }
        }

//        long time2 = System.currentTimeMillis();


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
//        long time3 = System.currentTimeMillis();
        resultsWriter.registerSuccessfulCrawl(domain, bugsFound);

//        System.out.println("getTime = " + (time2 - time1));
//        System.out.println("calculateTime = " + (time3 - time2) + " for " + watchedElements.size() + "els");


    }
}
