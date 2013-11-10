package info.tss.netassistant.process

import info.tss.netassistant.store.SqLiteManager
import info.tss.netassistant.store.structure.WebChange
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
    public static final String IE_10_USER_AGENT_HEADER = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36";
    public static final int DEFAULT_SOCKET_TIMEOUT = TimeUnit.MINUTES.toMillis(1);
    public static final int REQUEST_REPEATS_ON_ERRORS = 3;

    private Logger log = LoggerFactory.getLogger(NetFilter.class);
    SqLiteManager man;

    NetFilter(SqLiteManager man) {
        this.man = man;
    }

    // main method to request sources.
    public void filterResources() {
        def startTime = System.currentTimeMillis()

        for (WebChange wc : man.getAllWebChanges()) {
            def attempts = 0;
            while (!makelRequest(wc) || attempts++ < REQUEST_REPEATS_ON_ERRORS);
        }

        log.info("_Total filtering executing time(min): " + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTime));
    }

    private boolean makelRequest(WebChange wc) {
        try {
            Document detailDoc = Jsoup.connect(wc.url).timeout(DEFAULT_SOCKET_TIMEOUT)
                    .userAgent(IE_10_USER_AGENT_HEADER).get();
            println detailDoc.text();
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

}
