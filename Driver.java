import Parser.Parser;
import Parser.SAXHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Comparator;
import java.util.List;


public class Driver {

    public static void main(String[] args){
        long startTime = System.nanoTime();
        SAXHandler userhandler = null;
        //TODO REVERT to args 30,34,18
        File temp = new File("output");
        File temp2 = new File("postingList");
        try{
        temp.createNewFile();
        } catch(IOException e){
          e.printStackTrace();
        }
        try{
          temp2.createNewFile();
        } catch(IOException e){
          e.printStackTrace();
        }
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            userhandler = new SAXHandler();
            saxParser.parse("src\\Data\\wiki-search-small.xml", userhandler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        System.out.println("Parser.Parser Time : " + Double.toString((endTime - startTime)/1e9));

        long startTime1 = System.nanoTime();
        List<File> l = null;
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String r1, String r2) {
                if(r1 == null || r2 == null){
                    return 1;
                }

                return r1.split("[:]")[0].compareTo(r2.split("[:]")[0]);
            }
        };
        try {
            l = ExternalMerge.sortInBatch(new File("postingList"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ExternalMerge.mergeSortedFiles(l, new File("nOutput"),comparator,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime2 = System.nanoTime();
        System.out.println("Merge Time : " + Double.toString((endTime2 - startTime1)/1e9));
    }
}
