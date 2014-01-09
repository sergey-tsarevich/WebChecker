package info.tss.netassistant.process

import info.tss.netassistant.notify.ChangesNotifier
import info.tss.netassistant.store.SqLiteManager
import info.tss.netassistant.store.structure.WebChange
import info.tss.netassistant.ui.ViewHelper
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import org.jsoup.select.NodeVisitor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit

/**
 * Make filtering and processing from resourceList.
 * Author: tss
 * Date: 22.02.2013
 */
public class NetFilter {
    public static final String USER_AGENT_HEADER = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36";
    public static final int DEFAULT_SOCKET_TIMEOUT = TimeUnit.SECONDS.toMillis(30);
    public static final int REQUEST_REPEATS_ON_ERRORS = 2;
    public static NetFilter INST = new NetFilter();
    private static Logger log = LoggerFactory.getLogger(NetFilter.class);
    private static final int LINE_WIDTH = 80
    public static final String UNSUPPORTED_HTML_TAGS = "script,iframe,noscript,object,style,meta"

    public static void requestAndSave(webChangesList) {
        // todo: parralelize me!
        log.info("Start requesting!")
        def startTime = System.currentTimeMillis()
        webChangesList.each{wch-> //WebChange
            def attempts = 0;
            while (!makelRequest(wch) && ++attempts < REQUEST_REPEATS_ON_ERRORS);
        }
        log.info("Total request time: " + (System.currentTimeMillis() - startTime) / 1000 + " s.")
        SqLiteManager.SL.saveWebChangesList(Arrays.asList(webChangesList))
    }

    private static boolean makelRequest(WebChange wc) {
        try {
            log.info("Request url: $wc.url")
            def startTime = System.currentTimeMillis()
            def url = ViewHelper.autoCompleteUrl(wc.url)
            Document detailDoc = Jsoup.connect(url).timeout(DEFAULT_SOCKET_TIMEOUT)
                    .userAgent(USER_AGENT_HEADER)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip,deflate,sdch")
                    .header("Accept-Language", "en-US,en;q=0.8")
                    .header("Cache-Control", "no-cache")
                    .header("DNT", "1")
                    .header("Pragma", "no-cache")
                    .get();
            def currTxt = ""
            def currHtml = ""
            detailDoc.select(UNSUPPORTED_HTML_TAGS).remove();
            if (wc.filter){
                Elements adAttrs = detailDoc.select(wc.filter);
                currTxt = INST.html2text(adAttrs)
                currHtml = adAttrs.outerHtml()
            } else {
                currTxt = INST.html2text(detailDoc)
                currHtml = detailDoc.outerHtml()
            }
            wc.last_check = new Date()
            if (currTxt && currTxt!=wc.curr_txt) {
                wc.prev_txt = wc.curr_txt
                wc.curr_txt = currTxt
                wc.prev_html = wc.curr_html
                wc.curr_html = currHtml
                wc.viewed = 0
                ViewHelper.calcDiffs(wc)
                ChangesNotifier.notifyAllChannels(wc)
            }
            log.info("Requesting time: " + (System.currentTimeMillis() - startTime) / 1000 + " s.")
            return true;
        } catch (SocketTimeoutException s) { //repeat read
            log.error("!!!Timeout skip: " + s.message);
            return false;
        } catch (HttpStatusException hs) {
            if (HttpURLConnection.HTTP_NOT_FOUND != hs.statusCode) {
                log.error("!!!HTTP Error ${hs.statusCode}: " + hs.message);
                return true;
            }
            return false;
        }
    }

    public String html2text(def doc) {
        def resultStr = ""
        doc.traverse(new NodeVisitor() {
            def currLine = ""
            @Override
            void head(org.jsoup.nodes.Node node, int depth) {
                if (node instanceof TextNode){
                    currLine += ((TextNode)node).text() + "\t"
                    if (currLine.size() > LINE_WIDTH * 0.7) {
                        currLine = currLine.replaceAll("\\s{2,}", "\t")
                        resultStr += currLine + "\n"
                        currLine = ""
                    }
                }
            }

            @Override
            void tail(org.jsoup.nodes.Node node, int depth) {}
        });

        return resultStr;
    }

}
