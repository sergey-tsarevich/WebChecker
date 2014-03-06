package netassistanTest

import info.tss.netassistant.process.NetFilter
import info.tss.netassistant.store.structure.Diff
import info.tss.netassistant.ui.ViewHelper
import name.fraser.neil.plaintext.diff_match_patch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.safety.Whitelist

import java.util.concurrent.TimeUnit

/**
 * Test of JSOUP! 
 * Tss.
 * Date: 27.02.13
 */
public class HttpFilterTest extends GroovyTestCase {

    @Override
    protected void setUp() throws Exception {
    }

    public void testJsoupParsing() throws IOException {
        Document doc = Jsoup.connect("http://test.com/").timeout(1000).
                userAgent("Mozilla/5.0").get();
        assertNotNull(doc.text())
    }

    public void testLongTextParsing(){
        String p = new File("test/groovy/prev.txt").text;
        String c = new File("test/groovy/curr.txt").text;
        def diff = new diff_match_patch()
        List<diff_match_patch.Diff> diffs = diff.diff_main(p, c);
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
//        println addedTxt
    }

    public void testHtml2Text(){
        def f = "test/groovy/tst.html"
        Document doc = Jsoup.parse(new File(f),"UTF-8");
        def rez = NetFilter.INST.html2text(doc);
        assertNotNull(rez);
//        println rez;
    }

    public void testResponseFormatting(){
        def f = "test/groovy/tst.html"
        Document doc = Jsoup.parse(new File(f), "windows-1251");
        Elements adAttrs = doc.select("ul.recentposts");
        adAttrs.select("script,iframe,noscript,object").remove()
        def currTxt = NetFilter.INST.html2text(adAttrs)
        assertNotNull(currTxt);
    }


	// test html comparing
	public void testEkaterinkaResponse(){
		String p = new File("test/groovy/diffs/ek_p.html").text;
		String c = new File("test/groovy/diffs/ek_c.html").text;
		Diff d = ViewHelper.getColorizedHtml(p,c);
//		println d.fullText
		println d.addedText
	}
	
	
    // test html comparing
    public void testHtmlCleaning(){
        String p = new File("test/groovy/diffs/ek_p.html").text;
        String sbafe = Jsoup.clean(p,
                new Whitelist()
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
                .addProtocols("img", "src", "http", "https"));
		assertNotNull(safe);
//        println safe;
	}

    public void testTmpQueries() {
        def f = "test/groovy/n.html"
        Document doc = Jsoup.parse(new File(f), "UTF-8");
        NetFilter.prefixUrlsWithBase(doc, "http://sobor.by/")
        println doc.outerHtml()
    }

}
