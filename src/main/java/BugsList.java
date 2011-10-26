import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import com.google.common.base.Joiner;

public class BugsList {

    Pattern motherPattern;
    final Map<Pattern, String> bugPatterns = new HashMap<Pattern, String>();

    public BugsList(String fileLocation) {

        initialisePatterns(fileLocation);
    }

    private void initialisePatterns(String fileLocation) {
        FileInputStream fstream = null;
        try { fstream = new FileInputStream(fileLocation); } catch (FileNotFoundException e) {}

        BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));

        List<String> allRegexesList = new ArrayList<String>();

        String bugLine;
        try {
                while((bugLine = reader.readLine()) != null) {
                    String[] parts = bugLine.split(":");
                    String name = parts[1];
                    String regex = parts[0];

                    allRegexesList.add(regex);

                    Pattern pattern = Pattern.compile(regex);
                    bugPatterns.put(pattern, name);
                }
            } catch (IOException e) {}

        String motherRegex = Joiner.on("|").join(allRegexesList);
        motherPattern = Pattern.compile(motherRegex);
    }
}
