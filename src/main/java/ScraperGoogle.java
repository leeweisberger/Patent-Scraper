import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by leeweisberger on 7/8/16.
 */
public class ScraperGoogle {
    public static void main(String[] args) {
        Set<String> patents = CSVReader.readCSV("patents.csv");
        Set<String> patentsDone = CSVReader.readCSV("patents_done.csv");
        FileWriter writer=null;
        try {
            writer = new FileWriter("output.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String patent : patents){
            if(patentsDone.contains(patent)){
                continue;
            }
            String url = "http://www.freepatentsonline.com/"+patent +".html";
            System.out.println(url);
            try {
                Connection connection = Jsoup.connect(url);

                //specify user agent
                connection.userAgent("Mozilla/5.0");
                connection.referrer("http://www.google.com");
                //get the HTML document
                Document doc = connection.get();

//                Document doc = Jsoup.connect(url)
//                        .userAgent("Chrome")
//                        .get();

                List<String> inventors = getInventorsFromDoc(doc);
                List<String> claims = new ArrayList<String>();
                Patent finishedPatent = new Patent(patent,inventors,claims);

                writePatentToCSV(finishedPatent,writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

   /* private static List<String> getInventorsFromDoc(Document doc) {
        List<String> inventors = new ArrayList<String>();
        Elements inventorElements = doc.select("dd[itemprop=inventor]");
        for(Element inventorElement : inventorElements){
            inventors.add(inventorElement.text());
        }
        return inventors;
    }*/

     private static List<String> getInventorsFromDoc(Document doc) {
        List<String> inventors = new ArrayList<String>();
        Elements inventorElements = doc.select("meta[scheme='inventor']");
        for(Element inventorElement : inventorElements){
            String name = inventorElement.attr("content");
            String[] names = name.split(" ");
            StringBuilder nameBuilder = new StringBuilder();
            for(int i=1; i<names.length; i++){
                nameBuilder.append(names[i] + " ");
            }
            nameBuilder.append(names[0]);
            inventors.add(nameBuilder.toString().replace(",",""));
        }
        return inventors;
    }

    private static List<String> getClaimsFromDoc(Document doc) {
        List<String> claims = new ArrayList<String>();
        Elements claimElements = doc.select("div.claim-text");
        for(Element inventorElement : claimElements){
            claims.add(inventorElement.text());
        }
        return claims;
    }
    private static void writePatentToCSV(Patent patent, FileWriter writer){
        try {

            writer.append(StringEscapeUtils.escapeCsv(patent.getNumber()));
            writer.append(",");
            for(String author : patent.getInventors2()){
                writer.append(StringEscapeUtils.escapeCsv(author));
                writer.append(",");
            }
            for(String claim : patent.getClaims()){
                writer.append(StringEscapeUtils.escapeCsv(claim));
                writer.append(",");
            }
            writer.append("\n");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
