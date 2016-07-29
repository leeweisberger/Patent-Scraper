import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by leeweisberger on 7/8/16.
 */
public class Scraper {
    public static void main(String[] args) {
        List<Patent> finishedPatents = new ArrayList<Patent>();
        Set<String> patents = CSVReader.readCSV("patents.csv");
        FileWriter writer=null;
        try {
            writer = new FileWriter("output.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String patent: patents){
            String url = "http://patft.uspto.gov/netacgi/nph-Parser?Sect1=PTO1&Sect2=HITOFF&d=PALL&p=1&u=%2Fnetahtml%2FPTO%2Fsrchnum.htm&r=1&f=G&l=50&s1="+patent+".PN.&OS=PN/"+patent+"&RS=PN/"+patent;
            System.out.println(url);
            try {
                Document doc = Jsoup.connect(url).get();
                String inventorsString = getInventorsFromDoc(doc);
                if(inventorsString==null)continue;
                Map<String,String> inventors = parseInventors(inventorsString);

                String claimsString = getClaimsFromDoc(doc);
                if(claimsString==null)continue;
                List<String> claims = parseClaimes(claimsString);
                Patent finishedPatent = new Patent(patent,inventors,claims);
                finishedPatents.add(finishedPatent);
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

    private static List<String> parseClaimes(String claimsString) {
        List<String> claims = new ArrayList<String>();
        String pattern = "\\d+\\. (.*?)(?=\\d+\\. .*?)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(claimsString);
        while (m.find( )) {
            claims.add(m.group());
        }
        //find the last claim
        int lastClaim = claims.size()+1;
        String pattern2 = lastClaim+".*";
        Pattern p2 = Pattern.compile(pattern2);
        Matcher m2 = p2.matcher(claimsString);
        try {
            m2.find();
            claims.add(m2.group());
            return claims;
        }
        catch (Exception e){
            System.out.println("woopsies!!!!");
            return claims;
        }

    }

    private static String getClaimsFromDoc(Document doc) {
        try {
            Element body = doc.body();
            String claimsText = body.text().split("Claims")[1];
            claimsText = claimsText.split("Description")[0];
            return claimsText;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String getInventorsFromDoc(Document doc) {
        try {
            Element inventorHeaderElement = doc.getElementsContainingOwnText("Inventors:").get(0);
            Element inventorNamesElement = inventorHeaderElement.nextElementSibling();
            return inventorNamesElement.text();
        }
        catch (Exception e){

            e.printStackTrace();
            return null;
        }
    }

    private static Map<String,String> parseInventors(String inventorsString) {
        Map<String, String> inventors = new HashMap<String, String>();
        String[] inventorArray = inventorsString.split("\\),");
        for(String inventor:inventorArray){
            String[] splitInventor = inventor.split("\\(");
            String inventorName = splitInventor[0];
            String inventorLocation=splitInventor[1];
            inventorLocation=inventorLocation.replace(")", "");
            if(inventorName.contains(";")) {
                String[] inventorNameSplit = inventorName.split(";");
                inventorName = inventorNameSplit[1] + inventorNameSplit[0];
            }
            inventors.put(inventorName.trim(), inventorLocation.trim());
        }
        return inventors;
    }

    private static void writePatentToCSV(Patent patent, FileWriter writer){
        try {

            writer.append(StringEscapeUtils.escapeCsv(patent.getNumber()));
            writer.append(",");
            for(String author : patent.getInventors().keySet()){
                writer.append(StringEscapeUtils.escapeCsv(author));
                writer.append(",");
                writer.append(StringEscapeUtils.escapeCsv(patent.getInventors().get(author)));
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
