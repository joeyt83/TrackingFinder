import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class PageFetcher {

    Document getHtml(String url) throws IOException {
        return Jsoup.connect(url).timeout(10000).get();
    }
}
