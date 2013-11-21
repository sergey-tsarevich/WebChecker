package info.tss.netassistant.ui

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


    static List<String> getColorizedHtml(String prev_txt, String curr_txt){
        def resultList = []
        if (!prev_txt || !curr_txt) return resultList
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
                    addedTxt += df.text + "\n"
                    fullTxt += "<span style='background-color:#b0ffa0'>" + df.text + "</span>"
                    break
                case diff_match_patch.Operation.DELETE:
                    delTxt += df.text + "\n"
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




}
