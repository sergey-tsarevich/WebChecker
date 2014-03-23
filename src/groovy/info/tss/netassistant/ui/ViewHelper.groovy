package info.tss.netassistant.ui

import info.tss.netassistant.store.structure.Diff
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
     * @return list of Diff
     */
    static Diff getColorizedHtml(String prev_txt, String curr_txt){
        def resultDiff = new Diff();
        if (!prev_txt || !curr_txt) {
			resultDiff.fullText = curr_txt
            return resultDiff
        }
        def var = new diff_match_patch()
        List<diff_match_patch.Diff> diffs = var.diff_main(prev_txt, curr_txt);
        var.diff_cleanupSemantic(diffs);
		def fullTxt = "";
        def addedTxt = "";
        def delTxt = "";
        def linkOld, linkNew
        def lastOpenLinkIdx
        def linkMode = false
        diffs.each { df ->
            switch ( df.operation ) {
                case diff_match_patch.Operation.EQUAL:
                    lastOpenLinkIdx = df.text.lastIndexOf("<a")
                    if (linkMode || lastOpenLinkIdx > df.text.lastIndexOf("</a>")) {
                        if(!linkMode) {
                            linkMode = true
                            linkNew = linkOld = df.text.substring(lastOpenLinkIdx);
                            fullTxt += df.text.substring(0,lastOpenLinkIdx) + "\n"
                            break //return
                        }
                        def endLinkIdx = df.text.indexOf('</a>')
                        if (endLinkIdx >= 0) {
                            linkNew += df.text.substring(0,endLinkIdx) + '</span></a><br>'
                            linkOld += df.text.substring(0,endLinkIdx) + '</span></a><br>'

                            def idx = linkNew.indexOf('>'); // end of <a tag
                            linkNew = linkNew.substring(0, idx+1).replaceAll("\n","") + "<span class='ch_add'>" + linkNew.substring(idx+1)
                            fullTxt += linkNew
                            addedTxt += linkNew + "\n"

                            def idxO = linkOld.indexOf('>'); // end of <a tag
                            linkOld = linkOld.substring(0, idxO+1).replaceAll("\n","") + "<span class='ch_del'>" + linkOld.substring(idxO+1)
                            fullTxt += linkOld
                            delTxt += linkOld + "\n"

                            def remainTxt = df.text.substring(endLinkIdx + '</a>'.length());
                            lastOpenLinkIdx = remainTxt.lastIndexOf("<a")
                            if (lastOpenLinkIdx > remainTxt.lastIndexOf("</a>")) {
                                linkNew = linkOld = remainTxt.substring(lastOpenLinkIdx);
                                fullTxt += remainTxt.substring(0,lastOpenLinkIdx) + "\n"
                            } else {
                                fullTxt += remainTxt + "\n"
                                linkMode = false;
                            }
                        } else {
                            linkNew +=df.text
                            linkOld +=df.text
                        }
                    } else {
                        fullTxt += df.text + "\n"
                    }
                    break
                case diff_match_patch.Operation.INSERT:
                    if (linkMode) {
                        linkNew += df.text
                    } else {
                        addedTxt += df.text + "\n"
                        fullTxt += "<span class='ch_add'>" + df.text + "</span>"
                    }
                    break
                case diff_match_patch.Operation.DELETE:
                    if (linkMode) {
                        linkOld += df.text
                    } else {
                        delTxt += df.text + "\n"
                        fullTxt += "<span class='ch_del'>" + df.text + "</span>"
                    }
                    break
                default:    break
            }
        }
        resultDiff.fullText = fullTxt
        resultDiff.addedText = addedTxt
        resultDiff.deletedText = delTxt

        return resultDiff;
    }

    static void calcDiffs(WebChange wch, boolean onlyFull){
        // todo: check if its rendering is ok -> if not use flag to mark to handle html
//        def resultList = getColorizedHtml(wch.prev_txt, wch.curr_txt)
        def resultDiff = getColorizedHtml(wch.prev_html, wch.curr_html)
        wch.fullTxt = resultDiff.fullText ?:"";
        if (onlyFull) return;
        if(resultDiff.addedText) wch.added_txt += GuiManager.CHANGED_TEXT_SEPARATOR + resultDiff.addedText
        if(resultDiff.deletedText) wch.deleted_txt += GuiManager.CHANGED_TEXT_SEPARATOR + resultDiff.deletedText
    }


}
