package info.tss.netassistant.notify

import org.jsoup.Jsoup;

class ParseUtils {
	public static final String CHANGED_TEXT_SEPARATOR = "\b";
	
	public static String getLastAdding(String addings){
		if (addings) {
			def lastAdded = addings.split(CHANGED_TEXT_SEPARATOR)[-1] 
			return Jsoup.parse(lastAdded)?.text();
		} else {
			return "";
		}
	}
	
	
}
