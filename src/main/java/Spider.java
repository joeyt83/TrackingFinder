import java.util.Date;

public class Spider {

    public static void main(String[] args) {

        BugsList bugsList =  new BugsList("src/main/resources/bugs.csv");
        UrlQueue queue = new UrlQueue("src/main/resources/top-100k.csv");
        ResultsWriter writer = new ResultsWriter("results.txt");

        int numThreads = 400;
        for(int i = 0; i < numThreads; i++) {
            CrawlerWorker worker = new CrawlerWorker(queue, writer, new PageFetcher(), bugsList);
            new Thread(worker).start();
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
