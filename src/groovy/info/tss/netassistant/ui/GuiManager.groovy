package info.tss.netassistant.ui

import groovy.swing.SwingBuilder
import info.tss.netassistant.process.NetFilter
import info.tss.netassistant.store.SqLiteManager
import info.tss.netassistant.store.structure.WebChange
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.BorderFactory
import javax.swing.DefaultListModel
import javax.swing.ImageIcon
import javax.swing.JEditorPane
import javax.swing.JOptionPane
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.StyleSheet
import java.awt.Desktop

import static java.awt.BorderLayout.*
import static javax.swing.JSplitPane.VERTICAL_SPLIT
import static javax.swing.WindowConstants.EXIT_ON_CLOSE

/**
 * Author: Tss
 * Date: 01.11.13
 */
class GuiManager {
    private static SqLiteManager sqlMan
    private static SwingBuilder swing
    private static final Logger log = LoggerFactory.getLogger(GuiManager.class);

    public static void buildUI(SqLiteManager sMan) {
        sqlMan = sMan; // init
        swing = new SwingBuilder()

        DefaultListModel listModel = new DefaultListModel();
        def refreshUrlsList = {
            listModel.clear();
            sqlMan.getAllWebChanges().each {
                listModel.addElement(it);
                ViewHelper.calcDiffs(it);
            }
        }

        def currentWCh = null
        def showMsg = { msg, color ->
            swing.infoLbl.text = "<html><font color='${color}'>${msg}</font></html>";
            new Timer().runAfter(5000) { swing.infoLbl.text = "" }
        }

        refreshUrlsList()

        swing.actions() {
            action(id: 'selectAction', closure: { e ->
                def l = e.source
                if (!e.valueIsAdjusting) {
                    WebChange w = l.selectedValue
                    if (w) { // can be null on multi selection
                        currentWCh = w
                        swing.urlFld.text = w.url?:""
                        swing.filterFld.text = w.filter?:""
                        swing.viewedChBox.selected = w.viewed
                        swing.fullTxtPane.text = w.fullTxt?:""
                        if (w.added_txt && !w.viewed) swing.changesPane.text = "<html>" + w.added_txt.split("\b").join("<hr><br>") + "</html>"
                        else swing.changesPane.text = ""
                    };
                }
            })
            action(id: 'addAction', closure: { e ->
                def url = swing.urlFld.text;
                if (InputValidator.isUrlAvailable(url) ) {
                    def change = new WebChange(url: url, filter: swing.filterFld.text)
                    change.id = sqlMan.createOrUpdateWChange(change);
                    NetFilter.request([change].toArray())
                    showMsg('Added.', 'green')
                    refreshUrlsList()
                } else {
                    showMsg('Url is not available!', 'red')
                }
            })
            action(id: 'delAction', closure: { e ->
                if (JOptionPane.showConfirmDialog(null, "Are you sure to delete selected urls?")) return;
                urlsList.selectedValues.each{
                    sqlMan.deleteWChange(it.id)
                    log.info('Deleting webchange id: ' + it.id)
                    listModel.removeElement(it)
                }
            })
            action(id: 'reqAction', closure: { e ->
                def webChanges
                if(urlsList.selectedValues.size()){
                    webChanges = urlsList.selectedValues
                } else {
                    webChanges = listModel.toArray()
                }
                if (!webChanges.every{ it.viewed }) return JOptionPane.showMessageDialog(null, 'Review selected items first!')

                NetFilter.request(webChanges)
                refreshUrlsList()
            })
            action(id: 'changeViewed', closure: { e ->
                def l = e.source
                if (currentWCh) {
                    sqlMan.updateWChangeViewed(currentWCh.id, l.selected)
                    currentWCh.viewed = l.selected ? 1 : 0
                } else JOptionPane.showMessageDialog(null, 'Choose item first!')
            })
        }

        def frame = swing.frame(title: '<44>', size: [640, 480], defaultCloseOperation: EXIT_ON_CLOSE, show: true,
                iconImage: new ImageIcon(swing.class.classLoader.getResource('4.png')).image) {
            panel {
                borderLayout()
                panel(constraints: NORTH, border: BorderFactory.createTitledBorder('url:')) {
                    borderLayout()
                    textField(id: 'urlFld')
                }
                splitPane(constraints: CENTER, orientation: VERTICAL_SPLIT, dividerLocation: 150) {
                    panel {
                        borderLayout()
                        panel(constraints: WEST) {
                            borderLayout()
                            label(constraints: NORTH, id: 'infoLbl', text: '')
                            panel(constraints: CENTER){
                                borderLayout()
                                checkBox(constraints: CENTER, id: 'viewedChBox', text: 'Viewed', actionPerformed: changeViewed.closure)
                                textField(constraints: SOUTH, id: 'filterFld')
                            }
                            panel(constraints: SOUTH){
                                borderLayout()
                                panel(constraints: NORTH){
                                    button('Request', id: 'reqBtn', actionPerformed: reqAction.closure)
                                }
                                panel(constraints: CENTER){
                                    button('Add', id: 'addBtn', actionPerformed: addAction.closure)
                                    button('Delete', id: 'delBtn', actionPerformed: delAction.closure)
                                }
                            }
                        }
                        scrollPane(constraints: CENTER, border: BorderFactory.createTitledBorder('added: ')) {
                            textPane(id: 'changesPane', contentType: 'text/html')
                        }
                    }
                    panel {
                        borderLayout()
                        list(constraints: WEST, id: 'urlsList', model: listModel, valueChanged: selectAction.closure)

                        scrollPane(constraints: CENTER, border: BorderFactory.createTitledBorder('content: ')) {
                            textPane(id: 'fullTxtPane', contentType: 'text/html')
                        }
                    }
                }
            }
        }
        // stylish
        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("html {color:#000; font-family:times; margin: 4px; }");
        swing.fullTxtPane.editorKit = kit
        addLinkHandling(swing.fullTxtPane)
        addLinkHandling(swing.changesPane)

        adjustFrameSize(frame)
        centerOnScreen(frame)
    }

    def static centerOnScreen(component) {
        def paneSize = component.size
        def screenSize = component.toolkit.screenSize
        int x = (screenSize.width - paneSize.width) / 2
        int y = (screenSize.height - paneSize.height) * 0.45
        component.setLocation(x, y)
    }

    def static adjustFrameSize(component) {
        def screenSize = component.toolkit.screenSize
        int x = screenSize.width * 0.7
        int y = screenSize.height * 0.7
        component.setSize(x, y)
    }

    def static addLinkHandling(txtpane) {
        txtpane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        txtpane.setEditable(false);
        txtpane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) { // supported from Java 6
                        def url = e.getURL();
                        if (!url) {
                            url = swing.urlFld.text + "/" +e.description
                        }
//                        Desktop.getDesktop().browse(url); // works for windows
                        Desktop.getDesktop().browse(url.toURI()); // works for linux, java 7 may be?
                    }
                }
            }
        });
    }

}
