import java.util.Date;

public class Spider {

    public static void main(String[] args) {

        UrlQueue queue = new UrlQueue("src/main/resources/top-100k.csv");
        ResultsWriter writer = new ResultsWriter("results.txt");

        int numThreads = 1000;
        for(int i = 0; i < numThreads; i++) {
            CrawlerWorker worker = new CrawlerWorker(queue, writer, new PageFetcher(), new BugsList("src/main/resources/bugs.csv"));
            new Thread(worker).start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
