package info.tss.netassistant.process

import info.tss.netassistant.store.SqLiteManager
import info.tss.netassistant.store.structure.WebChange
import info.tss.netassistant.ui.InputValidator
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
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
    public static final int DEFAULT_SOCKET_TIMEOUT = TimeUnit.MINUTES.toMillis(1);
    public static final int REQUEST_REPEATS_ON_ERRORS = 3;
    private static Logger log = LoggerFactory.getLogger(NetFilter.class);

    private static boolean makelRequest(WebChange wc) {
        try {
            def url = InputValidator.autoCompleteUrl(wc.url)
            Document detailDoc = Jsoup.connect(url).timeout(DEFAULT_SOCKET_TIMEOUT)
                    .userAgent(USER_AGENT_HEADER).get();
            def currTxt = detailDoc.text();
            wc.last_check = new Date()
            if (currTxt && currTxt!=wc.curr_txt){
                wc.prev_txt = wc.curr_txt
                wc.curr_txt = currTxt
                def currWords = wc.curr_txt.split("\\s+")
                def prevWords = wc.prev_txt.split("\\s+")
                def diffAddedArr = currWords - prevWords
                def diffDeletedArr = prevWords - currWords
                wc.added_txt = diffAddedArr.join(" ")
                wc.viewed = 0
                wc.deleted_txt = diffDeletedArr.join(" ")
            }
            // todo: add diff parser
//        Elements adAttrs = detailDoc.select("#content tr[class!=header]");
//        mapScript.eachMatch(/YMaps.GeoPoint\(([\d\.]+)[\s,]*([\d\.]+)/) { geoCoords << it}
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

    public static void request(webChangesList) {
        // todo: parralelize me!
        webChangesList.each{wch-> //WebChange
            def attempts = 0;
            while (!makelRequest(wch) || attempts++ < REQUEST_REPEATS_ON_ERRORS);
        }
        SqLiteManager.SL.saveWebChangesList(Arrays.asList(webChangesList))
    }

}
