import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import com.google.common.base.Joiner;

public class BugsList {

    private Pattern motherPattern;
    private final Map<Pattern, String> bugPatterns = new HashMap<Pattern, String>();

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
                    String regexWithWildcard = ".*" + regex + ".*";

                    allRegexesList.add(regexWithWildcard);

                    Pattern pattern = Pattern.compile(regexWithWildcard);
                    bugPatterns.put(pattern, name);
                }
            } catch (IOException e) {}

        String motherRegex = Joiner.on("|").join(allRegexesList);
        motherPattern = Pattern.compile(motherRegex);
    }

    public String getBugNameForString(String pattern) {
        String namedBug = null;
        for(Map.Entry<Pattern, String> e: bugPatterns.entrySet()) {
          Pattern p = e.getKey();
            if(p.matcher(pattern).matches()) {
                namedBug = e.getValue();
                break;
            }
        }
        return namedBug;
    }

    public int getnumberOfBugs() {
        return bugPatterns.size();
    }

    public boolean hasMatchingPatterns(String resource) {
        return motherPattern.matcher(resource).matches();
    }
}
