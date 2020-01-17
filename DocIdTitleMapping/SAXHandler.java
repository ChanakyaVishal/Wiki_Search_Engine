package DocIdTitleMapping;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.HashSet;


public class SAXHandler extends DefaultHandler{


    private String curTag = null;
    private boolean revision = false;

	private StringBuilder curTitle;
    private StringBuilder curID;

    @Override
	public void startElement(String uri, String name, String qName, Attributes attr) {
        curTag = qName;
        if (qName.equals("revision")) {
            revision = true;
        } else if (qName.equals("page")) {
            getIdTitleMap.count++;
			curTitle = new StringBuilder("");
            curID = new StringBuilder("");
        }
	}

    @Override
    public void characters(char ch[], int start, int length) {
        if (curTag.equals("title")) {
            curTitle.append(ch, start, length);
        } else if (curTag.equals("id") && !revision){
            curID.append(ch, start, length);
        }
    }

	@Override
	public void endElement(String uri, String name, String qName) {
        if (qName.equals("page")) {
            if(curID.toString().length() >= 1 && curTitle.toString().length() >= 1) {
                FileWriter.getWriter().println(curID.toString().trim() + ":" + curTitle.toString().trim());
            }
        }
        if (qName.equals("revision")) {
            revision = false;
        }
    }

}
