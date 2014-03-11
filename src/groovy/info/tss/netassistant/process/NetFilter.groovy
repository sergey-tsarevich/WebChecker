package info.tss.netassistant.process

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.json.StringEscapeUtils
import info.tss.netassistant.notify.ChangesNotifier
import info.tss.netassistant.store.SqLiteManager
import info.tss.netassistant.store.structure.WebChange
import info.tss.netassistant.ui.ViewHelper
import org.jsoup.Connection
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode
import org.jsoup.safety.Whitelist
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
	public static final Whitelist HTML_CLEANER = new Whitelist()
                        .addTags(
                        "a", "b", "blockquote", "br", "cite", "code", "dd", "dl", "dt", "em",
                        "i", "li", "ol", "p", "pre", "q", "small", "strike", "strong", "sub",
                        "sup", "u", "ul")
                        .addAttributes("a", "href")
                        .addAttributes("blockquote", "cite")
                        .addAttributes("q", "cite")
//                        .addProtocols("a", "href", "ftp", "http", "https", "mailto")
                        .addProtocols("blockquote", "cite", "http", "https")
                        .addProtocols("cite", "cite", "http", "https")
//                        .addEnforcedAttribute("a", "rel", "nofollow")
                .addTags("img")
                .addAttributes("img", "align", "alt", "height", "src", "title", "width")
                .addProtocols("img", "src", "http", "https");

	

    public static void requestNotifyAndSave(webChangesList) {
        // todo: parralelize me!
        log.info("Start requesting!")
        def startTime = System.currentTimeMillis()
        webChangesList.each{wch-> //WebChange
            def attempts = 0;
            try {
                while (!makelRequest(wch) && ++attempts < REQUEST_REPEATS_ON_ERRORS);
            } catch(Exception e){
                log.error("Exception during request $wch.url : ", e);
            }
        }
        log.info("Total request time: " + (System.currentTimeMillis() - startTime) / 1000 + " s.")
        SqLiteManager.SL.saveWebChangesList(Arrays.asList(webChangesList))
    }

    private static boolean makelRequest(WebChange wc) {
        try {
            log.info("Request url: $wc.url")
            def startTime = System.currentTimeMillis()
            def url = ViewHelper.autoCompleteUrl(wc.url)
            Connection.Response response = Jsoup.connect(url).timeout(DEFAULT_SOCKET_TIMEOUT)
                    .userAgent(USER_AGENT_HEADER)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip,deflate,sdch")
                    .header("Accept-Language", "en-US,en;q=0.8")
                    .header("Cache-Control", "no-cache")
                    .header("DNT", "1")
                    .header("Pragma", "no-cache")
                    .ignoreContentType(true).execute();
            def currTxt = ""
            def currHtml = ""

            if (response.contentType().trim().indexOf("application/json")==0) { // JSON response
                JsonSlurper jslurper = new JsonSlurper();
                def parsedJson =  jslurper.parseText(response.body());
                if (parsedJson instanceof List && parsedJson.size() > 0 && parsedJson.get(0) instanceof HashMap) {
                    def str = ""
                    parsedJson.each { o->
                        o.each { k, v ->
                            if (v) str += k + " : " + v + "<br>"
                        }
                        str +="<br>"
                    }
                    currHtml = str;
                    currTxt = Jsoup.parse(str).text();
                } else {
                    currHtml = currTxt = StringEscapeUtils.unescapeJava(JsonOutput.prettyPrint(response.body()));
                }
            } else {        // HTML response
                Document detailDoc = response.parse();
                detailDoc.select(UNSUPPORTED_HTML_TAGS).remove();
                if (wc.filter){
                    Elements adAttrs = detailDoc.select(wc.filter);
                    currTxt = INST.html2text(adAttrs)
                    currHtml = adAttrs.outerHtml()
                } else {
                    currTxt = INST.html2text(detailDoc)
                    currHtml = detailDoc.outerHtml()
                }
            }
			currHtml = Jsoup.clean(currHtml, HTML_CLEANER);
            wc.last_check = new Date()
            if(currTxt) currTxt.trim()
            if(wc.curr_txt) wc.curr_txt.trim()

            if (currTxt && !currTxt.equals(wc.curr_txt)) {
                wc.prev_txt = wc.curr_txt
                wc.curr_txt = currTxt
                wc.prev_html = wc.curr_html
                wc.curr_html = prefixUrlsWithBase(currHtml, wc.url)
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

    /*
     * Prefix all locale links in docHtml with base url.
     * @return html with absolute urls in links
     */
    public static String  prefixUrlsWithBase(String docHtml, String base) {
        Document doc = Jsoup.parse(docHtml, "UTF-8");
        base = ViewHelper.autoCompleteUrl(base);
        Elements ems = doc.select("a[href~=^[\\/]?\\w+/]"); // select links with local patch
        ems.each {
            String url =  it.attr("href");
			String fullUrl = base + (base.endsWith("/") || url.startsWith("/") ? "": "/" ) + url
            it.attr("href", fullUrl)
        }

        return doc.outerHtml();
    }

}
