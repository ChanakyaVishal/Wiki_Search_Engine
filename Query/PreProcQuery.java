package Query;

import PreProcess.Stemmer;
import PreProcess.StopWordList;

import java.util.*;

public class PreProcQuery {

        private Stemmer stemmer;
        private HashMap<String, Integer> postingList;
        private String query;


        private String cleanUp(String text){
            text = text.replaceAll("[^0-9a-zA-Z:]+"," ");
            return text;
        }

        /**
         * Split the term with the delimiter being space :b
         *
         */
        private List<String> tokenize(String text){
            String[] temp = text.split("\\s+");
            HashMap<String,String> valToType = new HashMap<>();
            List<String> output = new ArrayList<>();
            //Variable to store the current type ("-b","-t" etc)
            String curVal = "";
            for(String ele: temp) {
                //Code to Change the current Type
                if(ele.contains(":")){
                    if(!curVal.equals(ele)){
                        curVal = ele;
                    }
                } else if(ele.length() >= 2){
                    output.add(curVal + ele);
                }
            }
            return output;
        }

        /**
         * Case-Folding
         *
         */
        private String caseFold(String text){
                try {
                    text = text.toLowerCase();
                }catch(NullPointerException e){
                    return null;
                }
            return text;
        }

        /**
         * Create New String Array with only the non-NULL elements
         *
         */
        private String stopWordRemoval(String text){
                for (String stop : StopWordList.stopWords) {
                    if (text.equals(stop)) {
                            return "";
                    }
            }
            return text;
        }

        /**
         * Stemming
         *
         */
        private String stem(String text){
                if (text != null) {
                    stemmer = new Stemmer();
                    for (int i = 0; i < text.length(); i++) {
                        stemmer.add(text.charAt(i));
                    }
                    stemmer.stem();
                    return stemmer.toString();
                }
            return text;
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

            /*
             * Sort the elements of the Text Box in alphabetical order
             */
            Map<String, Integer> sortedPostingList = new TreeMap<String, Integer>(postingList);
            return sortedPostingList;
        }

        public HashSet<String> processQuery(){
            String text = cleanUp(this.query);
            List<String> temp = tokenize(text);
            HashSet<String> finalHash = new HashSet<>();

            for (String ele : temp) {
                if (ele.length() >= 2) {
                    String tempCaseFold = caseFold(ele);
                    String tempStopWord = stopWordRemoval(tempCaseFold);
                    String tempStem = stem(tempStopWord);
                    if(tempStem != null){
                        finalHash.add(tempStem);
                    }
                }
            }
            /*DEBUG
            Set set2 = finalHash.entrySet();
            for (Object aSet : set2) {
                Map.Entry mentry2 = (Map.Entry) aSet;
                if (mentry2.getKey().toString().length() >= 2) {
                    System.out.println(mentry2.getKey() + " " + mentry2.getValue());
                }
            }*/

            return finalHash;
        }


        public PreProcQuery(String val) {
            this.query = val;
        }
    }


