import java.util.Date;

public class Spider {

    public static void main(String[] args) {

        BugsList bugs = new BugsList("src/main/resources/bugs.csv");
        UrlQueue queue = new UrlQueue("src/main/resources/top-1k.csv");
        ResultsWriter writer = new ResultsWriter("results.txt");

        System.out.println(new Date());
        int numThreads = 200;
        for(int i = 0; i < numThreads; i++) {
            CrawlerWorker worker = new CrawlerWorker(queue, writer, new PageFetcher(), new BugsList("src/main/resources/bugs.csv"));
            new Thread(worker).start();
        }
    }
}
