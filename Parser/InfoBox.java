package Parser;

import java.util.List;

public class InfoBox {

    private String infoBoxWikiText = null;
    private InfoBox infoBox = null;

    public String getInfoBoxWikiText() {
        return infoBoxWikiText;
    }

    public InfoBox(String infoBoxWikiText){

        if (infoBoxWikiText != null){
            this.infoBoxWikiText = infoBoxWikiText;
        } else {
            this.infoBoxWikiText = "";
        }

    }

    public InfoBox getInfoBox(String wikiText){
        if (infoBox == null)
            infoBox = parseInfoBox(wikiText);
        return infoBox;
    }

    private InfoBox parseInfoBox(String wikiText) {
        List<Integer> temp = processFunction("Infobox");
        if(temp == null) return new InfoBox(null);
        String infoBoxText = wikiText.substring(temp.get(0), temp.get(1) + 1);
        infoBoxText = stripCite(infoBoxText);
        return new InfoBox(infoBoxText);
    }


    private String stripCite(String text) {
        List<Integer> temp = processFunction("cite");
        if(temp == null) return text;
        text = text.substring(0, temp.get(0) - 1) + text.substring(temp.get(1));
        return stripCite(text);
    }

    private List<Integer> processFunction(String text){
        String CONST_STR = "{{" + text;
        int startPos = text.indexOf(CONST_STR);
        if (startPos < 0) return null;
        int bracketCount = 2;
        int endPos = startPos + CONST_STR.length();
        while(endPos < text.length()) {
            if (text.charAt(endPos) == '}') {
                bracketCount--;
            } else if (text.charAt(endPos) == '{'){
                bracketCount++;
            }
            if (bracketCount == 0) break;
            endPos++;
        }
        List<Integer> temp = null;
        temp.add(startPos);
        temp.add(endPos);
        return temp;
    }
}
