package Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryVariables {
    private HashMap<String, Integer> termDocCount = null;
    private HashMap<String,HashMap<Integer, Integer>> temp = null;
    private int docSize = 5311;
    private List<Integer> intersectedList = null;
    private List<String> tokens = null;

    public int getTermDocCount(String key){
        return termDocCount.get(key);
    }

    public int getDocSize() {
        return docSize;
    }

    public void setDocSize(int docSize) {
        this.docSize = docSize;
    }

    public List<Integer> getIntersectedList() {
        return intersectedList;
    }

    public void setIntersectedList(List<Integer> intersectedList) {
        this.intersectedList = intersectedList;
    }

    public HashMap<String, HashMap<Integer, Integer>> getTemp() {
        return temp;
    }

    public void appendTemp(String key, HashMap<Integer, Integer> val) {
        if(this.temp == null){
            this.temp = new HashMap<String, HashMap<Integer, Integer>>();
        }
        this.temp.putIfAbsent(key, val);
    }

    public HashMap<String, Integer> getTermDocCount() {
        return termDocCount;
    }

    public void appendTermDocCount(String key, Integer val) {
        if(this.termDocCount == null){
            this.termDocCount = new HashMap<>();
        }
        this.termDocCount.putIfAbsent(key, val);
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public void addTokens(String token) {
        if(tokens == null){
            tokens = new ArrayList<>();
        }
        tokens.add(token);
    }
}
