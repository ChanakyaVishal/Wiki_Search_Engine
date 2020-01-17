package Parser;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {

    private static Pattern catPattern = Pattern.compile("\\[\\["+ "Category" + ":(.*?)\\]\\]", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static Pattern redirectPattern = Pattern.compile("#REDIRECT"+"\\s*\\{\\{(.*?)\\}\\}", Pattern.CASE_INSENSITIVE);
    private static Pattern linkPattern = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.MULTILINE);
    public static Pattern stylesPattern = Pattern.compile("\\{\\|.*?\\|\\}$", Pattern.MULTILINE | Pattern.DOTALL);
    public static Pattern zeroPattern = Pattern.compile("[0*]+", Pattern.MULTILINE | Pattern.DOTALL);
    public static Pattern refClean = Pattern.compile("<ref>.*?</ref>", Pattern.MULTILINE | Pattern.DOTALL);
    public static Pattern curlyClean = Pattern.compile("^\\{\\{.*?\\}\\}$", Pattern.MULTILINE | Pattern.DOTALL);
    public static Pattern Cleanup1 = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.MULTILINE | Pattern.DOTALL);
    public static Pattern stubPattern = Pattern.compile("\\-"+"stub"+"\\}\\}", Pattern.CASE_INSENSITIVE);
    public static Pattern disambiguationPattern = Pattern.compile("\\{\\{"+ "disambiguation" +"\\}\\}", Pattern.CASE_INSENSITIVE);
    public static Pattern refPattern = Pattern.compile("\\[http://(.*?)\\]$", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public Parser() {
    }


    public WikiPageData cleanText(WikiPageData curPage){
        String text = curPage.getText();
        text = text.replaceAll("&gt;", ">");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&ndash;", "-");
        text = text.replaceAll("&quot;", " ");
        text = text.replaceAll("</?.*?>", " ");
        text = refClean.matcher(text).replaceAll(" ");

        parseCategories(curPage);
        parseLinks(curPage);
        findRedirect(curPage);
        processReferences(curPage);
        InfoBox temp = new InfoBox("");
        curPage.setInfoBox(temp.getInfoBox(curPage.getText()));
        Matcher matcher;

        /*matcher = stubPattern.matcher(curPage.getText());
        curPage.setStub(matcher.find());
        matcher = disambiguationPattern.matcher(curPage.getText());
        curPage.setDisambig(matcher.find());*/

        text = linkPattern.matcher(text).replaceAll(" ");
        text = redirectPattern.matcher(text).replaceAll(" ");
        text = refPattern.matcher(text).replaceAll(" ");

        text = curlyClean.matcher(text).replaceAll(" ");
        text = stylesPattern.matcher(text).replaceAll(" ");
        text = zeroPattern.matcher(text).replaceAll(" ");
        text = SAXHandler.matcher(text).replaceAll(" ");
        text = cleanHeadings(text);

        /*text = refPattern.matcher(text).replaceAll(" ");
        text = catPattern.matcher(text).replaceAll(" ");*/

        Matcher m = Cleanup1.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            int i = m.group().lastIndexOf('|');
            String replacement;
            if (i > 0) {
                replacement = m.group(1).substring(i - 1);
            } else {
                replacement = m.group(1);
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        text = text.replaceAll("'{2,}", " ");
        text = stripBottomInfo(text, "See also");
        text = stripBottomInfo(text, "Notes");
        text = stripBottomInfo(text, "Further reading");
        text = stripBottomInfo(text, "References");

        curPage.setText(text.trim());
        return curPage;
    }

    private String stripBottomInfo(String text, String label) {
        Pattern bottomPattern = Pattern.compile("^=*\\s?" + label + "\\s?=*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = bottomPattern.matcher(text);
        if(matcher.find()) text = text.substring(0, matcher.start());
        return text;
    }

    private void findRedirect(WikiPageData curPage) {
        Matcher matcher = redirectPattern.matcher(curPage.getText());
        if (matcher.find()) {
            curPage.setRedirect(true);
            if (matcher.groupCount() == 1) {
                curPage.setRedirectString(matcher.group(1));
            }
        }
    }

    private void processReferences(WikiPageData curPage){
        HashSet<String> pageRefs = new HashSet<String>();
        Matcher matcher = refPattern.matcher(curPage.getText());
        while (matcher.find()) {
            String[] temp = matcher.group(1).split("\\|");
            pageRefs.add(temp[0]);
        }
        curPage.setPageReferences(pageRefs);
    }

    private void parseCategories(WikiPageData curPage) {
        HashSet<String> pageCats = new HashSet<String>();
        Matcher matcher = catPattern.matcher(curPage.getText());
        while (matcher.find()) {
            String[] temp = matcher.group(1).split("\\|");
            pageCats.add(temp[0]);
        }
        curPage.setPageCats(pageCats);
    }

    private void parseLinks(WikiPageData curPage) {
        HashSet<String> pageLinks = new HashSet<String>();
        Matcher matcher = linkPattern.matcher(curPage.getText());
        while (matcher.find()) {
            String[] temp = matcher.group(1).split("\\|");
            if (temp.length == 0) {
                continue;
            }
            String link = temp[0];
            if (!link.contains(":")) {
                pageLinks.add(link);
            }
        }
        curPage.setPageLinks(pageLinks);
    }

    private String cleanHeadings(String text) {
        Pattern startHeadingPattern = Pattern.compile("^=*", Pattern.MULTILINE);
        Pattern endHeadingPattern = Pattern.compile("=*$", Pattern.MULTILINE);
        text = startHeadingPattern.matcher(text).replaceAll("");
        text = endHeadingPattern.matcher(text).replaceAll("");
        return text;
    }
}
