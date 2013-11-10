package info.tss.netassistant.ui

import groovy.swing.SwingBuilder
import info.tss.netassistant.store.SqLiteManager
import info.tss.netassistant.store.structure.WebChange

import javax.swing.*

import static java.awt.BorderLayout.*
import static javax.swing.JSplitPane.VERTICAL_SPLIT
import static javax.swing.WindowConstants.EXIT_ON_CLOSE

/**
 * Author: Tss
 * Date: 01.11.13
 */
class GuiManager {
    private static SqLiteManager sqlMan

    public static void buildUI(SqLiteManager sMan) {
        sqlMan = sMan; // init
        def swing = new SwingBuilder()

        DefaultListModel listModel = new DefaultListModel();

        sqlMan.getAllWebChanges().each {
            listModel.addElement(it);
        }

        def currentWChId = 0;

        swing.actions() {
            action(id: 'selectAction', closure: { e ->
                def l = e.source
                if (!e.valueIsAdjusting) {
                    WebChange w = l.selectedValue
                    JOptionPane.showMessageDialog(null, w.id + " :z: " + w.url)
                    currentWChId = w.id;
                }
            })
            action(id: 'addAction', closure: { e ->
                int count = sqlMan.createOrUpdateWChange(new WebChange(url: swing.urlFld.text))
                if (count) {
                    JOptionPane.showMessageDialog(null, 'Added.')
                } else {
                    JOptionPane.showMessageDialog(null, 'Not added! View logs!')
                }
            })
            action(id: 'changeViewed', closure: { e ->
                def l = e.source
                if (currentWChId) {
                    JOptionPane.showMessageDialog(null, 'Choose item first!' + l.selected + " : " + currentWChId)
                    sqlMan.updateWChangeViewed(l.selected, currentWChId)

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
                            checkBox(constraints: CENTER, id: 'viewedChBox', text: 'Viewed', actionPerformed: changeViewed.closure)
                            button('Add', id: 'addBtn', constraints: SOUTH, actionPerformed: addAction.closure)
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
