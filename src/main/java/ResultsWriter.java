import com.google.common.base.Joiner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ResultsWriter {

    BufferedWriter writer;
    int resultCount = 0;
    private final Object lock = new Object();

    public ResultsWriter(String fileLocation) {
        try {
            this.writer = new BufferedWriter(new FileWriter(fileLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void registerSuccessfulCrawl(Website site, List<String> bugsFound) {
        String resultsLine = site.domain + ":[";
        resultsLine += Joiner.on(",").join(bugsFound);
        resultsLine += "]";

        writeResultsToFile(resultsLine);
    }

    void registerFailedCrawl(Website site) {
        String resultsLine = site.domain + ":CRAWL FAILED";

        writeResultsToFile(resultsLine);
    }

     private void writeResultsToFile(String resultsLine) {
        try {
            synchronized (lock) {
                writer.write(resultsLine + "\n");
                writer.flush();
                if(++resultCount % 100 == 0)
                    System.out.println(resultCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
