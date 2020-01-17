package DocIdTitleMapping;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class getIdTitleMap {
    public static int count = 0;

    public static void main(String[] args) {
        SAXHandler userhandler = null;
        long startTime = System.nanoTime();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            userhandler = new SAXHandler();
            saxParser.parse("src\\Data\\wiki-search-small.xml", userhandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        System.out.println((endTime - startTime)/1e9);
        System.out.println(count);
    }
}
