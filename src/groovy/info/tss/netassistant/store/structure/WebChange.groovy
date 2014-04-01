package info.tss.netassistant.store.structure

/**
 * Author: Tss
 * Date: 30.10.13
 */
class WebChange implements Comparable{
    def id
    def url                      // url to check for changes
    def filter                   // CSS or jquery-like selector syntax
    def last_check   // time of last check, long from 1970
    def prev_txt     // previous (html)text to compare
    def curr_txt     // current (html)text to compare
    def viewed = 0   // 0 and 1 - if change(s) was viewed
    def added_txt=""    // difference that was added
    def deleted_txt=""  // difference that was deleted
    def prev_html    // previous html to compare
    def curr_html    // current html to compare
    def check_period    // period after which url will be checked
    def notifications=""    // comma-separated notification types
	def headers=""    // format: "Header-Name: value CRLF"
	def tag =""    // tag to group and use as prefix for url

    def fullTxt      // full colored html for UI only

	
    public int compareTo(Object o) {
		return this.toString().compareTo(o.toString());
	}


	@Override
    String toString() {
        if(!viewed && (added_txt || deleted_txt)) {
            return "<html><font color='#F88017'>" + toPresentation() + "</font></html>";
        } else {
            return toPresentation();
        }
    }

    String toPresentation() {
        return (this.tag?:"") + this.url;
    }
}
