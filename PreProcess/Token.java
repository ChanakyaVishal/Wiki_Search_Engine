package PreProcess;


public class Token {
    private String val;
    private String type;

    private Token(String name, String type){
        this.val = name;
        this.type = type;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
