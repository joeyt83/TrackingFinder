public class Spider {

    public static void main(String[] args) {

        BugsList bugs = new BugsList("src/main/resources/bugs.csv");
        UrlQueue queue = new UrlQueue("src/test/resources/ten-urls.txt");
        ResultsWriter writer = new ResultsWriter("results.txt");

        new CrawlerWorker(queue, writer, new PageFetcher(), bugs).run();
    }
}
