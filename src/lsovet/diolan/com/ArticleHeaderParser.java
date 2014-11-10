package lsovet.diolan.com;

import java.util.ArrayList;
import java.util.List;

class ArticleHeaderParser{
	private final static String articleStartTag = "<article";
	private final static String articleEndTag = "</article>";
	
	private final static String titleStartTag = "class=\"entry-title\">";
	private final static String titleEndTag = "</header>";

	private final static String dateStartTag = "class=\"entry-date\">";
	private final static String dateEndTag = "</div>";

	private final static String authorStartTag = "class=\"entry-author author vcard\">";
	private final static String authorEndTag = "</div>";
	
	private final static String thumbnailStartTag = "class=\"entry-thumbnail\">";
	private final static String thumbnailEndTag = "</div>";
	
	private final static String textStartTag = "class=\"entry-excerpt\" align=\"justify\">";
	private final static String textEndTag = "</div>";
	
	private final static String idStartTag ="<article id=\"";
	private final static String idEndTag ="\"";
	
	
	private final static String HTML_PATTERN = "\\<.*?>";
	private final static String TITLE_END = "...&nbsp";

	
	private List<ArticleHeader> mHeaders = new ArrayList<ArticleHeader>();
    
    public List<ArticleHeader> parse(String data){
    	mHeaders.clear();
    	for(String articleStr: parseTag(data, articleStartTag, articleEndTag)){
    		parseArticleHeader(articleStr);
    	}
    	return mHeaders;
    }
    
    private void parseArticleHeader(String articleStr){
    	String serverId = getOneTag(articleStr,idStartTag, idEndTag, false);

    	String rawTitle = "<" +getOneTag(articleStr, titleStartTag, titleEndTag);
    	String link = getOneTag(rawTitle,"href=\"", "\">", false);
    	String title = Util.unescapeHTML(rawTitle.replaceAll(HTML_PATTERN,"").trim()).trim();
    	
    	String rawDate = "<" +getOneTag(articleStr, dateStartTag, dateEndTag);
    	String date = rawDate.replaceAll(HTML_PATTERN, "").trim(); 
    	
    	String rawAuthor = "<" +getOneTag(articleStr, authorStartTag, authorEndTag);
    	String author = rawAuthor.replaceAll(HTML_PATTERN, "").trim(); 
    	
    	String rawThumbnail = getOneTag(articleStr, thumbnailStartTag, thumbnailEndTag);
    	String thumbnail = getOneTag(rawThumbnail,"src=\"", "\"", false);

    	String rawText = "<" +getOneTag(articleStr, textStartTag, textEndTag);
    	String text = rawText.replaceAll(HTML_PATTERN, "").trim();
    	int endTitileIndex = text.indexOf(TITLE_END);
    	if(endTitileIndex != -1){
    		text = Util.unescapeHTML(text.substring(0, endTitileIndex)).replaceAll("[\n\r]", "").trim();
    	}
    	
    	ArticleHeader h = new ArticleHeader(serverId, title, date, author, thumbnail, text, link);
		mHeaders.add(h);
    }
    
    
  public static String getOneTag(String data, String startTag, String endTag){
	  return getOneTag(data, startTag, endTag, true);
  }
  
  public static String getOneTag(String data, String startTag, String endTag, boolean includeTags){
	  int index = data.indexOf(startTag);
	  if(index != -1){
		  final int endIndex = data.indexOf(endTag, index+ startTag.length());
		  if(endIndex != 1){
    			return data.substring(includeTags ? index : index+startTag.length(), includeTags ? endIndex+endTag.length() : endIndex);
		  }
	  }
	  return null;
  }
    
    
  public static List<String> parseTag(String data, String startTag, String endTag){
    	List<String> list = new ArrayList<String>();
    	int index=0;
    	while((index = data.indexOf(startTag, index)) != -1){
    		final int endIndex = data.indexOf(endTag, index+ startTag.length());
    		if(endIndex != 1){
    			list.add(data.substring(index, endIndex+endTag.length()));
    			index= endIndex + endTag.length(); 
    		} else {
    			// error
    			return list;
    		}	
    	}	    	
    	return list;
    }
  }