package info.tss.netassistant.test

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * Test of JSOUP!
 * Tss.
 * Date: 27.02.13
 */
public class HttpFilterTest extends GroovyTestCase {

    @Override
    protected void setUp() throws Exception {
//        System.setProperty("http.proxyHost", "94.76.126.156");// Ukraine -> nice
//        System.setProperty("http.proxyPort", "54321"); // 2s !!
    }

    public void testJsoupParsing() throws IOException {
        if (true) return;// todo: impl tests)

        Document doc = Jsoup.connect("http://realt.by/sale/flats/show/database/").
                userAgent("Mozilla/5.0").get();
        Elements links = doc.select(".link-single a");
        List<String> adIds = new ArrayList<String>();

        for (Element link : links) {
            String[] linkParts = link.attr("href").split("/");
            if (linkParts.length > 0) adIds.add(linkParts[linkParts.length - 1]);
        }

        System.out.println(adIds);
    }
}
