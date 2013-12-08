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

    public void testHtmlColorizerText(){
        String p = new File("test/groovy/prev.txt").text;
        String c = new File("test/groovy/curr.txt").text;
        def r = ViewHelper.getColorizedHtml(p,c)
        assertEquals("""Безвозмездно, то есть даром!\t
	13 476 сообщений	В Уручье свой Вивальди\t
	9236 сообщений	Рыбалка – дело клевое\t
	11 717 сообщений	Made in China\t
	2167
""", r[1])
    }

    public void testHtmlColorizerHtml(){
        String p = new File("test/groovy/p.htm").text;
        String c = new File("test/groovy/c.htm").text;
        def r = ViewHelper.getColorizedHtml(p,c)
        assertEquals("""58bafdef17c5c92beb1c1c5161e0d43c
e
1051943
Безвозмездно, то есть даром
13 476
й
35ca4ac53ca187b49d3a384396127095
1060274
В Уручье свой Вивальди
9236
4a815055b3cf5695e13d1e0616b971c6
e
3865287
Рыбалка – дело клевое
11 717
adca378116fea408bd125a0e030ed4bc
6810733
Made in China
2167
""", r[1])
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
        def f = "test/groovy/tst.html"
        Document doc = Jsoup.parse(new File(f), "windows-1251");
        Elements adAttrs = doc.select("ul.recentposts");
        adAttrs.select("script,iframe,noscript,object").remove()
        println adAttrs.outerHtml()
        def currTxt = NetFilter.INST.html2text(adAttrs)
        assertNotNull(currTxt);
    }

    public void testTmpQueries(){
        def f = "test/groovy/n.html"
        Document doc = Jsoup.parse(new File(f), "windows-1251");
        Elements adAttrs = doc.select("div[style^=width:640px]");
        adAttrs.select("script,iframe,noscript,object").remove()
        println adAttrs.outerHtml()
//        def currTxt = NetFilter.INST.html2text(adAttrs)
//        assertNotNull(currTxt);
    }


}
