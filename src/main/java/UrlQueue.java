import java.io.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UrlQueue {

    BufferedReader sitesList;
    Queue<Website> failedUrls = new ConcurrentLinkedQueue<Website>();
    private final Object lock = new Object();


    public UrlQueue(String fileLocation) {
        try {
            this.sitesList = new BufferedReader(new FileReader(fileLocation));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    Website getNextSite() {
        Website site = null;
        synchronized (lock) {
            try {
                String domain = sitesList.readLine();
                if (domain != null) {
                    site = new Website(domain);
                } else {
                    site = failedUrls.poll();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return site;
    }

    void returnFailedSiteToQueue(Website site) {
        failedUrls.add(site);
    }
}
