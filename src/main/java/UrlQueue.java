import java.io.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UrlQueue {

    BufferedReader sitesList;
    Queue<String> failedUrls = new ConcurrentLinkedQueue<String>();
    private final Object lock = new Object();
    int serveCount = 0;

    public UrlQueue(String fileLocation) {
        try {
            this.sitesList = new BufferedReader(new FileReader(fileLocation));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    String getNextUrl() {
        String url = null;
        synchronized (lock) {
            try {
                url = sitesList.readLine();
                if (url == null) {
                    url = failedUrls.poll();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (url != null) {
                serveCount++;
                if (serveCount % 50 == 0) {
                    System.out.println(serveCount);
                }
            }
        }
        return url;
    }

    void returnUrl(String url) {
        System.out.println(url);
        failedUrls.add(url);
    }
}
