package Parser;

import PreProcess.PreProc;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


public class SAXHandler extends DefaultHandler{


	private Parser parser;

    private String curTag = null;
    private boolean revision = false;

    private StringBuilder curWikitext;
	private StringBuilder curTitle;
    private StringBuilder curID;

    public static int counter = 0;

    private WikiPageData curPage = null;


    @Override
	public void startElement(String uri, String name, String qName, Attributes attr) {
        curTag = qName;
        if (qName.equals("revision")) {
            revision = true;
        } else if (qName.equals("page")) {
            counter++;
			curWikitext = new StringBuilder("");
			curTitle = new StringBuilder("");
            curID = new StringBuilder("");
            curPage = new WikiPageData();
        }
	}

    @Override
    public void characters(char ch[], int start, int length) {
        if (curTag.equals("title")) {
            curTitle = curTitle.append(ch, start, length);
        } else if (curTag.equals("text")){
            curWikitext = curWikitext.append(ch, start, length);
        } else if (curTag.equals("id") && !revision){
            curID = curID.append(ch, start, length);
        }
    }

	@Override
	public void endElement(String uri, String name, String qName) {

        if (qName.equals("page")) {
            parser = new Parser();
            curPage.setTitle(curTitle.toString());
            curPage.setId(curID.toString());
            curPage.setText(curWikitext.toString());
            curPage = parser.cleanText(curPage);

            //Send the page to the PreProcessor
            PreProc pre = new PreProc(curPage);
        }
        if (qName.equals("revision")) {
            revision = false;
        }
    }

}
