import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BugsList {

    Pattern motherPattern;
    Map<Pattern, String> bugPatterns = new HashMap<Pattern, String>();

    public BugsList(String fileLocation) {

        FileInputStream fstream = null;
        try { fstream = new FileInputStream(fileLocation); } catch (FileNotFoundException e) {}

        BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));

        String bugLine;
        try {
                while((bugLine = reader.readLine()) != null) {
                    String[] parts = bugLine.split(":");
                    String name = parts[1];

                    String regex = parts[0];
                    Pattern pattern = Pattern.compile(regex);

                    bugPatterns.put(pattern, name);
                }
            } catch (IOException e) {}
    }
}
