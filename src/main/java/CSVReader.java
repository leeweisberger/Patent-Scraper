import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by leeweisberger on 7/8/16.
 */
public class CSVReader {
    public static Set<String> readCSV(String fileName){
        String csvFile = fileName;
        BufferedReader br = null;
        String line="";
        Set<String> patents = new HashSet<String>();
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                patents.add(line);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return patents;
    }
}
