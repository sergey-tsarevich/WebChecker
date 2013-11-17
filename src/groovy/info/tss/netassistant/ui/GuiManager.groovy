package info.tss.netassistant.ui

import groovy.swing.SwingBuilder
import info.tss.netassistant.process.NetFilter
import info.tss.netassistant.store.SqLiteManager
import info.tss.netassistant.store.structure.WebChange
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.BorderFactory
import javax.swing.DefaultListModel
import javax.swing.JOptionPane

import static java.awt.BorderLayout.*
import static javax.swing.JSplitPane.VERTICAL_SPLIT
import static javax.swing.WindowConstants.EXIT_ON_CLOSE

/**
 * Author: Tss
 * Date: 01.11.13
 */
class GuiManager {
    private static SqLiteManager sqlMan
    private static final Logger log = LoggerFactory.getLogger(GuiManager.class);

    public static void buildUI(SqLiteManager sMan) {
        sqlMan = sMan; // init
        def swing = new SwingBuilder()

        DefaultListModel listModel = new DefaultListModel();
        def refreshUrlsList = {
            listModel.clear();
            sqlMan.getAllWebChanges().each {
                listModel.addElement(it);
            }
        }

        def currentWChId = 0;
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
                    if (w) {
                        currentWChId = w.id
                        swing.urlFld.text = w.url
                        swing.viewedChBox.selected = w.viewed
                    }; // can be null on multi selection
                }
            })
            action(id: 'addAction', closure: { e ->
                def url = swing.urlFld.text;
                if (InputValidator.isUrlAvailable(url) && sqlMan.createOrUpdateWChange(new WebChange(url: url))) {
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
                if(urlsList.selectedValues.size()){
                    NetFilter.request(urlsList.selectedValues)
                } else {
                    NetFilter.request(listModel.toArray())
                }
            })
            action(id: 'changeViewed', closure: { e ->
                def l = e.source
                if (currentWChId) {
                    JOptionPane.showMessageDialog(null, 'Choose item first!' + l.selected + " : " + currentWChId)
                    def f = sqlMan.updateWChangeViewed(currentWChId, l.selected)
                } else JOptionPane.showMessageDialog(null, 'Choose item first!')
            })
        }

        def frame = swing.frame(title: '<44>', size: [600, 800], defaultCloseOperation: EXIT_ON_CLOSE, show: true) {
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
                            checkBox(constraints: CENTER, id: 'viewedChBox', text: 'Viewed', actionPerformed: changeViewed.closure)
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
                            textPane(id: 'changesPane')
                        }
                    }
                    panel {
                        borderLayout()
                        list(constraints: WEST, id: 'urlsList', model: listModel, valueChanged: selectAction.closure)

                        scrollPane(constraints: CENTER, border: BorderFactory.createTitledBorder('content: ')) {
                            textPane(id: 'fullTxtPane')
                        }
                    }
                }
            }
        }

        centerOnScreen(frame)
    }

    def static centerOnScreen(component) {
        def paneSize = component.size
        def screenSize = component.toolkit.screenSize
        int x = (screenSize.width - paneSize.width) / 2
        int y = (screenSize.height - paneSize.height) * 0.45
        component.setLocation(x, y)
    }

}