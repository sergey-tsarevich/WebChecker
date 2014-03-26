package info.tss.netassistant.ui

import groovy.swing.SwingBuilder
import info.tss.netassistant.process.NetFilter
import info.tss.netassistant.store.SqLiteManager
import info.tss.netassistant.store.structure.WebChange
import info.tss.netassistant.notify.EmailChannel
import info.tss.netassistant.notify.ParseUtils;
import info.tss.netassistant.notify.SystemTrayChannel
import info.tss.netassistant.notify.JDialogChannel

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.DefaultListModel
import javax.swing.ImageIcon
import javax.swing.JEditorPane
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener
import javax.swing.text.html.HTMLEditorKit

import java.awt.Desktop
import java.awt.FlowLayout
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

import static java.awt.BorderLayout.*
import static javax.swing.JSplitPane.HORIZONTAL_SPLIT
import static javax.swing.JSplitPane.VERTICAL_SPLIT
import static javax.swing.WindowConstants.EXIT_ON_CLOSE
import static info.tss.netassistant.notify.ParseUtils.CHANGED_TEXT_SEPARATOR

/**
 * Author: Tss
 * Date: 01.11.13
 */
class GuiManager {
    private static final Logger log = LoggerFactory.getLogger(GuiManager.class);
    private static SqLiteManager sqlMan = SqLiteManager.SL;
    private static SwingBuilder swing = new SwingBuilder()
    private static DefaultListModel listModel = new DefaultListModel();
    private static WebChange currentWCh;

	public static void refreshUrlsList() {
        listModel.clear();
		sqlMan.getAllWebChanges().sort{it.toString()}.each {
            listModel.addElement(it);
            ViewHelper.calcDiffs(it, true);
        }
    }

    public static void buildUI() {
        def showMsg = { msg, color ->
            swing.infoLbl.text = "<html><font color='${color}'>${msg}</font></html>";
            new Timer().runAfter(5000) { swing.infoLbl.text = "" }
        }

		def getNotificationsList = { swing ->
			def notifications = "";
			notifications += swing['notifyChBox_' + SystemTrayChannel.TYPE].selected ? "," + SystemTrayChannel.TYPE : ""
			notifications += swing['notifyChBox_' + EmailChannel.TYPE].selected ? "," + EmailChannel.TYPE : ""
			notifications += swing['notifyChBox_' + JDialogChannel.TYPE].selected ? "," + JDialogChannel.TYPE : ""
			if (notifications) notifications = notifications -',';
			return notifications
		}

        refreshUrlsList()

        swing.actions() {
            action(id: 'selectAction', closure: { e ->
                def l = e.source
                if (!e.valueIsAdjusting) {
                    WebChange w = l.selectedValue
                    if (w) { // can be null on multi selection
                        currentWCh = w
						
						swing['notifyChBox_' + EmailChannel.TYPE].selected = false
						swing['notifyChBox_' + SystemTrayChannel.TYPE].selected = false
						swing['notifyChBox_' + JDialogChannel.TYPE].selected = false
						w.notifications?.split(",").each{ns->
							if(ns) swing['notifyChBox_' + ns].selected = true;
						}
                        swing.urlFld.text = w.url?:""
                        swing.filterFld.text = w.filter?:""
						swing.periodFld.text = w.check_period?:""
						swing.updatedLbl.text = w.last_check ? new Date(w.last_check).format("dd.MM.YYYY hh:mm") : ""
                        swing.viewedChBox.selected = w.viewed
						swing.headersPane.text = w.headers
						swing.tagFld.text = w.tag
						swing.fullTxtPane.text = "<html><head><style> .ch_add, .ch_add *{background-color:#b0ffa0;}.ch_del, .ch_del *{background-color:#ffa0a0;} </style></head><body style=\"margin:20px 40px;\">" + (w.fullTxt?:"") + "</body></html>"
                        if (w.added_txt && !w.viewed)
                            swing.changesPane.text = "<html><div  style=\"margin: 20px 40px;\">" +
                                    w.added_txt.split(CHANGED_TEXT_SEPARATOR).findAll{it}.join("<hr><br>") + "</div></html>"
                        else swing.changesPane.text = ""
                    };
                }
            })
            action(id: 'addAction', closure: { e ->
                def url = swing.urlFld.text;
                if (InputValidator.isUrlAvailable(url) ) {
                    def change = new WebChange(url: url, filter: swing.filterFld.text, check_period: swing.periodFld.text, 
						notifications: getNotificationsList(swing), headers: swing.headersPane.text, tag: swing.tagFld.text)
                    change.id = sqlMan.createOrUpdateWChange(change);
                    NetFilter.requestNotifyAndSave([change].toArray(), true)
                    showMsg('Added.', 'green')
                    refreshUrlsList()
                } else {
                    showMsg('Url is not available!', 'red')
                }
            })
			action(id: 'updAction', closure: { e ->
				def url = swing.urlFld.text;
				if (InputValidator.isUrlAvailable(url) ) {
					currentWCh.url = url
					currentWCh.filter = swing.filterFld.text
					currentWCh.check_period = swing.periodFld.text
					currentWCh.notifications = getNotificationsList(swing)
					currentWCh.headers = swing.headersPane.text
					currentWCh.tag = swing.tagFld.text
					sqlMan.createOrUpdateWChange(currentWCh);
					NetFilter.requestNotifyAndSave([currentWCh].toArray(), true)
					showMsg('Updated.', 'green')
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

                NetFilter.requestNotifyAndSave(webChanges, true)
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
			action(id: 'notificationCheck', closure: { e ->
				def selected = e.source.selected
				urlsList.selectedValues.each {
					it.notifications = getNotificationsList(swing);
					sqlMan.createOrUpdateWChange(it);
				}
			})
			action(id: 'popupMenuShow', closure: { e ->
				if (e.isPopupTrigger()) {
					popupMenu {
						menuItem{
							action(name:'Copy html content', closure:{
                                StringSelection selection = new StringSelection( "fullTxtPane".equals(e.source.name) ? currentWCh?.fullTxt : currentWCh?.added_txt);
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(selection, selection);
						    })
						}
					}.show(e.getComponent(), e.getX(), e.getY())
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
                                checkBox(id: 'viewedChBox', text: 'Viewed', actionPerformed: changeViewed.closure)
								button('Request', id: 'reqBtn', actionPerformed: reqAction.closure)
								button('Settings', id: 'settingsBtn', actionPerformed: settingsAction.closure)
								label(id: 'updatedLbl', text: '')
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
										textField(constraints: CENTER, id: 'periodFld') 
									}
									scrollPane(constraints: SOUTH, border: BorderFactory.createTitledBorder('"Header-Name: value CRLF": ')) {
										textPane(id: 'headersPane')
									}
                                }
                                panel(constraints: CENTER){
									borderLayout()
									panel(constraints: NORTH, border: BorderFactory.createTitledBorder('tag:')) {
										borderLayout()
										textField(constraints: CENTER, id: 'tagFld') 
									}
									panel(constraints: CENTER, border: BorderFactory.createTitledBorder('notifications:')) {
										checkBox(id: 'notifyChBox_'+ EmailChannel.TYPE, text: 'Email', actionPerformed: notificationCheck.closure)
										checkBox(id: 'notifyChBox_'+ SystemTrayChannel.TYPE, text: 'Tray', actionPerformed: notificationCheck.closure)
										checkBox(id: 'notifyChBox_'+ JDialogChannel.TYPE, text: 'Dialog', actionPerformed: notificationCheck.closure)
									}
									panel(constraints: SOUTH) {
	                                    button('Add', id: 'addBtn', actionPerformed: addAction.closure)
										button('Update', id: 'updBtn', actionPerformed: updAction.closure)
	                                    button('Delete', id: 'delBtn', actionPerformed: delAction.closure)
									}
                                }
                            }
                        }
                        scrollPane(constraints: CENTER, border: BorderFactory.createTitledBorder('added: ')) {
                            textPane(id: 'changesPane', contentType: 'text/html', mousePressed: popupMenuShow.closure)
                        }
                    }
                    splitPane (constraints: CENTER, orientation: HORIZONTAL_SPLIT, dividerLocation: 150){
						scrollPane(constraints: WEST) {
							list(id: 'urlsList', model: listModel, valueChanged: selectAction.closure)
						}
                        scrollPane(constraints: CENTER, border: BorderFactory.createTitledBorder('content: ')) {
                            textPane(id: 'fullTxtPane', contentType: 'text/html', mousePressed: popupMenuShow.closure)
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

        swing.urlsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    WebChange w = evt.source.selectedValue
                    if (Desktop.isDesktopSupported()) { // supported from Java 6
                        def url = ViewHelper.autoCompleteUrl(w.url);
                        Desktop.getDesktop().browse(url.toURI());
                    }
                }
            }
        });

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
                        Desktop.getDesktop().browse(url.toURI());
                    }
                }
            }
        });
    }

}
