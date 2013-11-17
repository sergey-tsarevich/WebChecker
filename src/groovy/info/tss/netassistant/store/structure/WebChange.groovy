package info.tss.netassistant.store.structure

/**
 * Author: Tss
 * Date: 30.10.13
 */
class WebChange {
    def id
    def url          // url to check for changes
    def filter       // Some filter, todo: think about it!
    def last_check   // time of last check
    def prev_txt     // previous (html)text to compare
    def curr_txt     // current (html)text to compare
    def viewed = 0   // 0 and 1 - if change(s) was viewed
    def added_txt    // difference that was added
    def deleted_txt  // difference that was deleted

    @Override
    String toString() {
        if(!viewed && (added_txt || deleted_txt)) {
            return "<html><font color='orange'>" + this.url + "</font><html>";
        } else {
            return this.url;
        }
    }
}
