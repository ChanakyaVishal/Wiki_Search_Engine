package PreProcess;


import Parser.FileWriter;
import Parser.WikiPageData;

import java.util.*;
import java.util.regex.Pattern;

public class PreProc {

    private Stemmer stemmer;
    private HashMap<String, Integer> postingList;
    private WikiPageData curPage;
    private String type;
    public static Pattern num_text = Pattern.compile("[A-Za-z]+[0-9]+", Pattern.MULTILINE | Pattern.DOTALL);
    private HashSet<String> mainMap = null;


    private String cleanUp(String text){
        text = text.replaceAll("[^0-9a-zA-Z]+"," ");
        return text;
    }

    /**
     * Split the term with the delimiter being space :b
     *
     */
    private String[] tokenize(String text){
        String[] temp = text.split("\\s+");
        //TODO Split [0-9]+[a-zA-Z]+
        /*for(String ele: temp){
            Matcher arr = num_text.matcher(text);
            for(int i=0;i<arr.end()-arr.start();i++){
                String val = arr.group(i);

            }
        }*/
        return temp;
    }

    /**
     * Case-Folding
     *
     */
    private String[] caseFold(String[] text){
        for (int i = 0; i < text.length; i++) {
            try {
                text[i] = text[i].toLowerCase();
            }catch(NullPointerException e){
                return null;
            }
        }
        return text;
    }

    /**
     * Create New String Array with only the non-NULL elements
     *
     */
    private HashSet<String> stopWordRemoval(String[] text){
        HashSet<String> temp = new HashSet<>();
        for (String subText : text) {
            for (String stop : StopWordList.stopWords) {
                if (!subText.equals(stop)) {
                    temp.add(subText);
                    break;
                }
            }
        }
        return temp;
    }

    /**
     * Stemming
     *
     */
    private void stem(HashSet<String> text){
        for (String element : text) {
            if (element != null) {
                stemmer = new Stemmer();
                for (int i = 0; i < element.length(); i++) {
                    stemmer.add(element.charAt(i));
                }
                stemmer.stem();
                mainMap.add(stemmer.toString() + type);
            }
        }
    }

    /**
     * Creates the Inverted Index
     *
     */
    private Map<String, Integer> createIndex(HashSet<String> text){
        /*
         * HashMap of the document
         */
        postingList = new HashMap<String, Integer>();

        for (String element : text) {
            postingList.putIfAbsent(element, 0);
            postingList.replace(element, postingList.get(element) + 1);
        }
        Map<String, Integer> sortedPostingList = new TreeMap<String, Integer>(postingList);

        return sortedPostingList;
    }

    /**
     * Prints
     * @param map
     */

    private void write(Map<String, Integer> map){
        Set set = map.entrySet();
        for (Object aSet : set) {
            Map.Entry mentry2 = (Map.Entry) aSet;
            if (mentry2.getKey().toString().length() >= 4) {
                FileWriter.getWriter().println(mentry2.getKey() + ":" + curPage.getId().trim() + "-" + mentry2.getValue());
            }
        }
    }

    private void accum(String text){
        text = cleanUp(text);
        String[] temp = tokenize(text);
        temp = caseFold(temp);
        HashSet<String> tempHash = stopWordRemoval(temp);
        stem(tempHash);
        return;
    }


    public PreProc(WikiPageData page) {
     mainMap = new HashSet<String>();
     this.curPage = page;
     type = "-b";
     accum(curPage.getText());
     type = "-i";
     accum(curPage.getInfoBox().toString());
     type = "-t";
     accum(curPage.getTitle());
     type = "-c";
     accum(curPage.getPageCats().toString());
     type = "-l";
     accum(curPage.getPageLinks().toString());
     type = "-r";
     accum(curPage.getPageReferences().toString());
     Map<String, Integer> tempMap = createIndex(mainMap);
     write(tempMap);
    }
}
