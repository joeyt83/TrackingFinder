import com.google.common.base.Joiner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ResultsWriter {

    BufferedWriter writer;
    private final Object lock = new Object();

    public ResultsWriter(String fileLocation) {
        try {
            this.writer = new BufferedWriter(new FileWriter(fileLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void registerSuccessfulCrawl(String url, List<String> bugsFound) {
        String resultsLine = url + ":[";
        resultsLine += Joiner.on(",").join(bugsFound);
        resultsLine += "]";

        writeResultsToFile(resultsLine);
    }

    void registerFailedCrawl(String url) {
        String resultsLine = url + ":CRAWL FAILED";
        writeResultsToFile(resultsLine);

    }

     private void writeResultsToFile(String resultsLine) {
        try {
            synchronized (lock) {
                writer.write(resultsLine + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
