package info.tss.netassistant.ui

import info.tss.netassistant.store.structure.WebChange
import name.fraser.neil.plaintext.diff_match_patch

/**
 * Helps to build UI presentation
 * Author: TsS
 * Date: 11/20/13
 */
public class ViewHelper {
//    static final diff_match_patch diff = new diff_match_patch();

    static String autoCompleteUrl(String url){
        url = url.trim();
        if (url.indexOf("http")!=0) {
            url = "http://" + url
        }

        return url;
    }

    /**
     * Add html colored format to diffs.
     * @param prev_txt - previous text
     * @param curr_txt - current text
     * @return list of three Strings:
     *         [0] << full text
     *         [1] << added text
     *         [2] << deleted text
     */
    static List<String> getColorizedHtml(String prev_txt, String curr_txt){
        def resultList = []
        if (!prev_txt || !curr_txt) {
            resultList << curr_txt
            return resultList
        }
        List<diff_match_patch.Diff> diffs = new diff_match_patch().diff_main(prev_txt, curr_txt);
        def fullTxt = "<html>";
        def addedTxt = "";
        def delTxt = "";
        diffs.each { df ->
            switch ( df.operation ) {
                case diff_match_patch.Operation.EQUAL:
                    fullTxt += df.text + "\n"
                    break
                case diff_match_patch.Operation.INSERT:
                    if(df.text && df.text.trim()) addedTxt += df.text + "\n"
                    fullTxt += "<span style='background-color:#b0ffa0'>" + df.text + "</span>"
                    break
                case diff_match_patch.Operation.DELETE:
                    if(df.text && df.text.trim()) delTxt += df.text + "\n"
                    fullTxt += "<span style='background-color:#ffa0a0'>" + df.text + "</span>"
                    break
                default:    break
            }
        }
        fullTxt += "</html>";
        resultList << fullTxt
        resultList << addedTxt
        resultList << delTxt

        return resultList;
    }

    static void calcDiffs(WebChange wch){
        // todo: check if its rendering is ok -> if not use flag to mark to handle html
//        def resultList = getColorizedHtml(wch.prev_txt, wch.curr_txt)
        def resultList = getColorizedHtml(wch.prev_html, wch.curr_html)
        if(resultList[1]) wch.added_txt =resultList[1]
        if(resultList[2]) wch.deleted_txt = resultList[2]
        wch.fullTxt = resultList[0]?:"";
    }





}
