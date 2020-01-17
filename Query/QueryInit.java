package Query;

import Parser.SAXHandler;
import org.omg.PortableInterceptor.INACTIVE;
import sun.awt.datatransfer.DataTransferer;

import java.io.*;
import java.util.*;

public class QueryInit {

    public static void main(String[] args){
        //String query = "b: atari b: archive";
        String query = args[0];
        //System.out.println(query);

        //Initializations
        String inputSearch = "";
        List<Integer> intersectedList = null;
        HashMap<String, Double> idfList = new HashMap<>();
        QueryVariables vars = new QueryVariables();
        HashMap<String, HashMap<Integer,Double>> doc_tfIdf = new HashMap<>();
        HashMap<String, Double> que_tfIdf = new HashMap<>();
        long stTime = System.nanoTime();

        //Pre_Process Query
        PreProcQuery preProcQuery = new PreProcQuery(query);
        HashSet<String> procQuery = preProcQuery.processQuery();

        //Create Token List
        for(String ele: procQuery) {
            String construct = reconstruct(ele);
            if(construct.split("-").length<2){
                construct += "-b";
            }
            vars.addTokens(construct);
        }

        //Get Intersection
       for (String ele:vars.getTokens()){
            if (ele.length() >= 2) {
                vars.appendTemp(ele,getDocIds(ele, vars));
                List<Integer> interTemp = new ArrayList<>();
                HashMap<Integer, Integer> cacheMap = vars.getTemp().get(ele);
                Set set = cacheMap.entrySet();
                for (Object aSet2 : set) {
                    Map.Entry mentry3 = (Map.Entry) aSet2;
                    interTemp.add(Integer.parseInt(mentry3.getKey().toString()));
                 }
                 //System.out.println(ele);
                 interTemp.sort(defaultcomparator);
                 intersectedList = union(interTemp,intersectedList);
            }
        }

        //Sort Intersection
        intersectedList.sort(defaultcomparator);

        //idf Calculation
        for (String ele:vars.getTokens()) {

            //System.out.println(ele);
            Integer value = vars.getTermDocCount(ele);
            idfList.putIfAbsent(ele,Math.log10(vars.getDocSize() / value));
        }

        //Query Term occurrence calculator
        HashMap<String,Integer> keyValueCount = new HashMap<>();
        for (String str:vars.getTokens()) {
            if(keyValueCount.containsKey(str)){
                keyValueCount.replace(str,keyValueCount.get(str) + 1);
            }else{
                keyValueCount.put(str,1);
            }
        }

        //doc_tf Calculation
        for (String str:vars.getTokens()) {
            HashMap<Integer,Integer> temp = vars.getTemp().get(str);
            for (Integer ele: intersectedList) {
                if(temp.containsKey(ele)){
                    if(doc_tfIdf.containsKey(str)){
                        HashMap<Integer, Double> tempArr = doc_tfIdf.get(str);
                        Double val = Math.log10(1 + temp.get(ele)) * idfList.get(str);
                        tempArr.put(ele,val);
                        doc_tfIdf.replace(str,tempArr);
                    }else{
                        HashMap<Integer, Double> tempArr = new HashMap<>();
                        Double val = Math.log10(1 + temp.get(ele)) * idfList.get(str);
                        tempArr.putIfAbsent(ele,val);
                        //System.out.println(str + tempArr);
                        doc_tfIdf.put(str,tempArr);
                    }
                } else {
                    if(doc_tfIdf.containsKey(str)) {
                        HashMap<Integer, Double> tempArr = doc_tfIdf.get(str);
                        Double val = 0.0;
                        tempArr.putIfAbsent(ele,val);
                        //System.out.println(str + tempArr);
                        doc_tfIdf.replace(str,tempArr);
                    }else{
                        HashMap<Integer, Double> tempArr = new HashMap<>();
                        Double val = 0.0;
                        tempArr.putIfAbsent(ele,val);
                        //System.out.println(str + tempArr);
                        doc_tfIdf.put(str,tempArr);
                    }
                }
            }
        }

        //que_tf calculation
        for (String ele:vars.getTokens()) {
            que_tfIdf.putIfAbsent(ele,idfList.get(ele) * Math.log10(1 + keyValueCount.get(ele)));
        }

        HashMap<Integer, Double> tempO = new HashMap<>();
        List<HashMap<String,HashMap<Integer,Double>>> championsList = new ArrayList<>();
        for(String val: vars.getTokens()){
            tempO = doc_tfIdf.get(val);

            HashMap<Integer, Double> finalTemp = tempO;
            Comparator<Integer> valueComparator = new Comparator<Integer>() {
                public int compare(Integer k1, Integer k2) {
                    if (finalTemp.get(k1).equals(finalTemp.get(k2)))
                        return 1;
                    else if(finalTemp.get(k1) > finalTemp.get(k2))
                        return -1;
                    else if(finalTemp.get(k1) < finalTemp.get(k2))
                        return 1;
                    return 0;
                }
            };
            Map<Integer, Double> sortedByValuesDoc = new TreeMap<Integer, Double>(valueComparator);
            HashMap<Integer,Double> counter = new HashMap<>();
            sortedByValuesDoc.putAll(tempO);
            Set printSet2 = sortedByValuesDoc.entrySet();
            int counterCalc = 0;
            for (Object ele : printSet2) {
                if (counterCalc == 20) break;
                Map.Entry mentry4 = (Map.Entry) ele;
                counter.putIfAbsent(Integer.parseInt(mentry4.getKey().toString()),Double.parseDouble(mentry4.getValue().toString()));
                counterCalc++;
            }
            HashMap<String,HashMap<Integer,Double>> championListCreator = new HashMap<>();
            championListCreator.putIfAbsent(val,counter);
            championsList.add(championListCreator);
        }
        List<Integer> intersectedListN = new ArrayList<>();
        for(HashMap chmpion: championsList){
            Set printSet2 = chmpion.entrySet();
            for (Object ele : printSet2) {
                Map.Entry mentry4 = (Map.Entry) ele;
                List<Integer> newPurelyTemp = new ArrayList<>();
                newPurelyTemp.sort(defaultcomparator);
                //System.out.println(mentry4.getKey() + " " + mentry4.getValue());
            }
            }

        //System.out.println(intersectedList);
        //for(Integer ele : intersectedListN){}

        //Calculate docVector and DocScore
        List<Double> a = new ArrayList<>();
        HashMap<Integer,Double> docVectors = new HashMap<>();
            for(HashMap hash: championsList){
                for(String val: vars.getTokens()){
                    HashMap<Integer,Double> purelyTemp = (HashMap<Integer, Double>) hash.get(val);
                    if(purelyTemp != null){
                        //System.out.println(purelyTemp);
                        Set printSet2 = purelyTemp.entrySet();
                        for (Object ele : printSet2) {
                            Map.Entry mentry4 = (Map.Entry) ele;
                            if(docVectors.containsKey(mentry4.getKey())){
                                Double tempA = docVectors.get(mentry4.getKey());
                                tempA += que_tfIdf.get(val) * Double.valueOf(mentry4.getValue().toString());
                                docVectors.replace(Integer.parseInt(mentry4.getKey().toString()),tempA);
                            }else{
                                Double tempA = 0.0;
                                tempA += (que_tfIdf.get(val) * Double.valueOf(mentry4.getValue().toString()));
                                docVectors.putIfAbsent(Integer.parseInt(mentry4.getKey().toString()),tempA);
                            }
                        }
                    }
                }
            }
        Comparator<Integer> valueComparator = new Comparator<Integer>() {
                    public int compare(Integer k1, Integer k2) {
                        int compare =
                                docVectors.get(k1).compareTo(docVectors.get(k2));
                        if (compare == 0)
                            return -1;
                        else
                            return -compare;
                    }
                };

        Map<Integer,Double> sortedByValues = new TreeMap<Integer,Double>(valueComparator);
        sortedByValues.putAll(docVectors);
        Set printSet2 = sortedByValues.entrySet();
        int count = 0;
        for (Object ele : printSet2) {
            Map.Entry mentry4 = (Map.Entry) ele;
            if(count == 10 )break;
            System.out.println(mentry4.getKey() + " " + getTitle(mentry4.getKey().toString()));
            count++;
        }
        //Sort by Que_tf-idf score
        /*Map<String, Double> sortedByValuesQue = new TreeMap<String, Double>(valueComparatorQue);
        sortedByValuesQue.putAll(doc_tfIdf);*/

        /*Set printSet = sortedByValuesDoc.entrySet();
        for (Object ele : printSet) {
            Map.Entry mentry4 = (Map.Entry) ele;
           System.out.println(mentry4.getKey() + " " + mentry4.getValue());
        }*/

        /*Set printSet2 = sortedByValuesQue.entrySet();
        for (Object ele : printSet2) {
            Map.Entry mentry4 = (Map.Entry) ele;
            System.out.println(mentry4.getKey() + " " + mentry4.getValue());
        }*/

        long enTime = System.nanoTime();
        System.out.println((enTime - stTime)/1e9);


    }

    public static String getTitle(String key){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("mapping"));
            String line = reader.readLine();
            while (line != null) {
                String[] values = line.split(":");
                if(values[0].equals(key)) return values[1];
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Comparator<Integer> defaultcomparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    };


    public static HashMap<Integer, Integer> getDocIds(String ele, QueryVariables vars){
        String line = "";
        if(ele.split("-").length<2) {
            ele = ele + "-b";
        }
        HashMap<Integer, Integer> output = new HashMap<>();
        int gapParser = 0;
        int termFrequency = 0;
        try {
                BufferedReader br = new BufferedReader(new FileReader("nOutput"));
                try {
                    while((line = br.readLine()) != null){
                        String[] words = line.split(":");
                        //System.out.println(words[1]);
                        if (words[1].equals(ele)) {
                                vars.appendTermDocCount(ele,Integer.parseInt(words[0]));
                                //System.out.println(ele + "[]" + words[0]);
                                String[] listPositions = words[2].split("\\|");
                                for(String element: listPositions){
                                    String[] val = element.split("-");
                                    termFrequency += Integer.parseInt(val[1]);
                                    gapParser += Integer.parseInt(val[0]);
                                    try {
                                        output.putIfAbsent(Integer.parseInt(String.valueOf(gapParser)),termFrequency);
                                        termFrequency = 0;
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                        }
                                }
                            }
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

    return output;
    }

    public double dotProduct(List<Double> a, List<Double> b){
        double val = 0;
        for(int i=0;i<a.size();i++){
            val += a.get(i) * b.get(i);
        }
        return val;
    }

    public double L2Norm(List<Double> a){
        double val = 0;
        for(int i=0;i<a.size();i++){
            val += a.get(i) * a.get(i);
        }
        return Math.sqrt(val);
    }


    public static String reconstruct(String text){
        String[] temp = text.split("[:]");
        if(temp.length>=2) {
            String output = temp[1] + "-" + temp[0];
            return output;
        }
        return text;
    }

    public static List<Integer> union(List<Integer> a, List<Integer> b){
        List<Integer> temp = new ArrayList<>();
        int countA = 0,countB = 0;
        if(a == null & b == null){
            return temp;
        }else if(a == null){
            return b;
        }else if(b == null){
            return a;
        }
        while(countA < a.size() && countB < b.size()){
            if(a.get(countA).equals(b.get(countB))){
                temp.add(a.get(countA));
                countA++;
                countB++;
            }else if(a.get(countA) > b.get(countB)){
                temp.add(b.get(countB));
                countB++;
            } else{
                temp.add(a.get(countA));
                countA++;
            }
        }
        return temp;
    }

}
