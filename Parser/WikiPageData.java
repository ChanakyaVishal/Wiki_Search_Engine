package Parser;


import java.util.HashSet;

/**
*
* 
* @author Chanakya Vishal
*
*/
public class WikiPageData {

    private String Id;
    private String title;
    private InfoBox infoBox;
    private String text;
    private HashSet<String> pageCats = null;
    private HashSet<String> pageLinks = null;
    private HashSet<String> pageReferences = null;
    private boolean redirect;
    private String redirectString;
    private boolean stub;
    private boolean disambig;
	
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }


    public String getRedirectString() {
        return redirectString;
    }

    public void setRedirectString(String redirectString) {
        this.redirectString = redirectString;
    }

    public InfoBox getInfoBox() { return infoBox; }

    public void setInfoBox(InfoBox infoBox) { this.infoBox = infoBox; }

    public HashSet<String> getPageCats() {
        return pageCats;
    }

    public void setPageCats(HashSet<String> pageCats) {
        this.pageCats = pageCats;
    }

    public HashSet<String> getPageLinks() {
        return pageLinks;
    }

    public void setPageLinks(HashSet<String> pageLinks) {
        this.pageLinks = pageLinks;
    }

    public boolean isStub() {
        return stub;
    }

    public void setStub(boolean stub) {
        this.stub = stub;
    }

    public boolean isDisambig() {
        return disambig;
    }

    public void setDisambig(boolean disambig) {
        this.disambig = disambig;
    }

    public HashSet<String> getPageReferences() {
        return pageReferences;
    }

    public void setPageReferences(HashSet<String> pageReferences) {
        this.pageReferences = pageReferences;
    }
}
