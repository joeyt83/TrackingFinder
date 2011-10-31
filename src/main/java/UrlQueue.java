import java.io.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UrlQueue {

    BufferedReader sitesList;
    Queue<String> failedUrls = new ConcurrentLinkedQueue<String>();

    public UrlQueue(String fileLocation) {
        try {
            this.sitesList = new BufferedReader(new FileReader(fileLocation));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    String getNextUrl() {
        String url = null;
        try {
            url = sitesList.readLine();
            if(url == null) {
                url = failedUrls.poll();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }

    void returnUrl(String url) {
        failedUrls.add(url);
    }
}
