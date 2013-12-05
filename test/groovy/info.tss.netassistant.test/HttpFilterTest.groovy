package info.tss.netassistant.test

import info.tss.netassistant.process.NetFilter
import info.tss.netassistant.ui.ViewHelper
import name.fraser.neil.plaintext.diff_match_patch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.concurrent.TimeUnit

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
        Document doc = Jsoup.connect("http://test.com/").timeout(1000).
                userAgent("Mozilla/5.0").get();
//        print doc.text();
        assertNotNull(doc.text())

        if (true) return;// todo: impl tests)
        Elements links = doc.select(".link-single a");
        List<String> adIds = new ArrayList<String>();

        for (Element link : links) {
            String[] linkParts = link.attr("href").split("/");
            if (linkParts.length > 0) adIds.add(linkParts[linkParts.length - 1]);
        }

        System.out.println(adIds);
    }

    public void testLongTextParsing(){
        String p = new File("test/groovy/prev.txt").text;
        String c = new File("test/groovy/curr.txt").text;
        def diff = new diff_match_patch()
        List<diff_match_patch.Diff> diffs = diff.diff_main(p, c);
//        println diff.diff_prettyHtml(diffs); //todo: check some bugz
        def addedTxt = "";
        diffs.each { df ->
            switch ( df.operation ) {
                case diff_match_patch.Operation.EQUAL:
                    break
                case diff_match_patch.Operation.INSERT:
                    if(df.text && df.text.trim()) addedTxt += df.text + "\n"
                    break
                case diff_match_patch.Operation.DELETE:
                    break
                default:    break
            }
        }
        println addedTxt
    }

    public void testHtmlColorizer(){
        String p = new File("test/groovy/prev.txt").text;
        String c = new File("test/groovy/curr.txt").text;
        def st = System.currentTimeMillis()
        def r = ViewHelper.getColorizedHtml(p,c)
        r[0]
        println TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - st)
//        println  (System.currentTimeMillis() - st) + " ms"
//        println r[0]
    }

    public void testHtml2Text(){
        def f = "test/groovy/tst.html"
        Document doc = Jsoup.parse(new File(f),"UTF-8");
        def rez = NetFilter.INST.html2text(doc);
        assertNotNull(rez);
        println rez;
    }

    public void testColorizedHtml(){

        ViewHelper.getColorizedHtml(wc.prev_txt, wc.curr_txt)

    }

    public void testResponseFormatting(){
        def f = "h:\\temp\\hh\\Белорусский православный информационный портал СОБОР.by.htm"
//        Document doc = Jsoup.parse(new File(f),"UTF-8");
        Document doc = Jsoup.parse(new File(f), "windows-1251");
//        Elements adAttrs = doc.select("#rightcol");
//        Elements adAttrs = doc.select("#sideone,#sidetwo,#sidethree,#rightcol div.r-div:eq(0),#rightcol div.r-div:eq(6)");
//        Elements adAttrs = doc.select("#rightcol div.r-div:eq(1)");
        Elements adAttrs = doc.select("a.menu-d");
        def currTxt = NetFilter.INST.html2text(adAttrs)

        assertNotNull(currTxt);
        println currTxt

    }


}
