package info.tss.netassistant.store.structure

/**
 * Author: Tss
 * Date: 30.10.13
 */
class WebChange {
    def id
    def url                      // url to check for changes
    def filter                   // CSS or jquery-like selector syntax
    def last_check = new Date()  // time of last check
    def prev_txt     // previous (html)text to compare
    def curr_txt     // current (html)text to compare
    def viewed = 0   // 0 and 1 - if change(s) was viewed
    def added_txt    // difference that was added
    def deleted_txt  // difference that was deleted
    def prev_html    // previous html to compare
    def curr_html    // current html to compare


    def fullTxt      // full colored html for UI only

    @Override
    String toString() {
        if(!viewed && (added_txt || deleted_txt)) {
            return "<html><font color='blue'>" + this.url + "</font></html>"; // #F88017 - orange
        } else {
            return this.url;
        }
    }
}
