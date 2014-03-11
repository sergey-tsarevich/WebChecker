package info.tss.netassistant.ui

import groovy.swing.SwingBuilder
import info.tss.netassistant.process.NetFilter
import info.tss.netassistant.store.SqLiteManager
import info.tss.netassistant.store.structure.WebChange
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.DefaultListModel
import javax.swing.ImageIcon
import javax.swing.JEditorPane
import javax.swing.JOptionPane
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener
import javax.swing.text.html.HTMLEditorKit
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.FlowLayout

import static java.awt.BorderLayout.*
import static javax.swing.JSplitPane.HORIZONTAL_SPLIT
import static javax.swing.JSplitPane.VERTICAL_SPLIT
import static javax.swing.WindowConstants.EXIT_ON_CLOSE

/**
 * Author: Tss
 * Date: 01.11.13
 */
class GuiManager {
    private static final Logger log = LoggerFactory.getLogger(GuiManager.class);
    private static SqLiteManager sqlMan = SqLiteManager.SL;
    private static SwingBuilder swing = new SwingBuilder()
    private static DefaultListModel listModel = new DefaultListModel();

    public static void refreshUrlsList() {
        listModel.clear();
        sqlMan.getAllWebChanges().each {
            listModel.addElement(it);
            ViewHelper.calcDiffs(it);
        }
    }

    public static void buildUI() {
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
                        swing.fullTxtPane.text = "<html><div  style=\"margin:20px 40px;\">" + (w.fullTxt?:"") + "</div></html>"
                        if (w.added_txt && !w.viewed)
                            swing.changesPane.text = "<html><div  style=\"margin: 20px 40px;\">" + w.added_txt.split("\b").join("<hr><br>") + "</div></html>"
                        else swing.changesPane.text = ""
                    };
                }
            })
            action(id: 'addAction', closure: { e ->
                def url = swing.urlFld.text;
                if (InputValidator.isUrlAvailable(url) ) {
                    def change = new WebChange(url: url, filter: swing.filterFld.text)
                    change.id = sqlMan.createOrUpdateWChange(change);
                    NetFilter.requestNotifyAndSave([change].toArray())
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
                if (webChanges.every{ !it.viewed && !it.prev_html}) return JOptionPane.showMessageDialog(null, 'Review selected items first!')

                NetFilter.requestNotifyAndSave(webChanges)
                refreshUrlsList()
            })
			action(id: 'settingsAction', closure: { e ->
				def visible = swing.togglePanel.visible;
				if (visible) {
					swing.relayoutPanel.layout = new BoxLayout(swing.relayoutPanel, BoxLayout.PAGE_AXIS);
				} else {
					swing.relayoutPanel.layout = new FlowLayout();
				}
				swing.togglePanel.visible = !visible;
			})
            action(id: 'changeViewed', closure: { e ->
                def selected = e.source.selected
                urlsList.selectedValues.each {
                    sqlMan.updateWChangeViewed(it.id, selected)
                    it.viewed = selected ? 1 : 0
                    it.deleted_txt = ""
                    it.added_txt = ""
                }
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
                            panel(constraints: CENTER, id: 'relayoutPanel'){
								//borderLayout()
                                checkBox(id: 'viewedChBox', text: 'Viewed', actionPerformed: changeViewed.closure)
								button('Request', id: 'reqBtn', actionPerformed: reqAction.closure)
								button('Settings', id: 'settingsBtn', actionPerformed: settingsAction.closure)
                            }
                            panel(constraints: SOUTH, id: 'togglePanel'){
                                borderLayout()
                                panel(constraints: NORTH){
									borderLayout()
									panel(constraints: NORTH, border: BorderFactory.createTitledBorder('filter:')) {
										borderLayout()
										textField(constraints: CENTER, id: 'filterFld')
									}
									panel(constraints: CENTER, border: BorderFactory.createTitledBorder('period in ms:')) {
										borderLayout()
										textField(constraints: CENTER, id: 'periodFld') // todo: make like a filterFld 
									}
									
                                }
                                panel(constraints: CENTER){
									borderLayout()
									panel(constraints: NORTH, border: BorderFactory.createTitledBorder('notifications:')) {
										checkBox(id: 'emailChBox', text: 'Email')
										checkBox(id: 'trayChBox', text: 'Tray')
										checkBox(id: 'dialogChBox', text: 'Dialog')
									}
									panel(constraints: CENTER) {
	                                    button('Add', id: 'addBtn', actionPerformed: addAction.closure)
	                                    button('Delete', id: 'delBtn', actionPerformed: delAction.closure)
									}
                                }
                            }
                        }
                        scrollPane(constraints: CENTER, border: BorderFactory.createTitledBorder('added: ')) {
                            textPane(id: 'changesPane', contentType: 'text/html')
                        }
                    }
                    splitPane (constraints: CENTER, orientation: HORIZONTAL_SPLIT, dividerLocation: 150){
						scrollPane(constraints: WEST) {
							list(id: 'urlsList', model: listModel, valueChanged: selectAction.closure)
						}
                        scrollPane(constraints: CENTER, border: BorderFactory.createTitledBorder('content: ')) {
                            textPane(id: 'fullTxtPane', contentType: 'text/html')
                        }
                    }
                }
            }
        }
        // stylish
        HTMLEditorKit kitFull = new HTMLEditorKit();
        kitFull.getStyleSheet().addRule("html {background-color:#f2f2f2; font-family:verdana; }");
        swing.fullTxtPane.editorKit = kitFull
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
                            url =  ViewHelper.autoCompleteUrl(swing.urlFld.text) + "/" +e.description
                        }
//                        Desktop.getDesktop().browse(url); // works for windows
                        Desktop.getDesktop().browse(url.toURI()); // works for linux, java 7 may be?
                    }
                }
            }
        });
    }

}
