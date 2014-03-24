import groovy.swing.SwingBuilder

import javax.swing.*
import static java.awt.BorderLayout.*
import static javax.swing.JSplitPane.HORIZONTAL_SPLIT
import static javax.swing.JSplitPane.VERTICAL_SPLIT
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE


DefaultListModel listModel = new DefaultListModel();
listModel.addElement("sasaddsad112d12d");
listModel.addElement("sasaddsad112d12222d");

SwingBuilder swing = new SwingBuilder()
def frame = swing.frame(title: '<44>', size: [640, 480], defaultCloseOperation: DISPOSE_ON_CLOSE, show: true) {
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
                        //                                borderLayout()
                        checkBox( id: 'viewedChBox', text: 'Viewed')
                        button('Request', id: 'reqBtn')
                        button('Settings', id: 'settingsBtn')
                    }
                    panel(constraints: SOUTH){
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
                            panel(constraints: NORTH, border: BorderFactory.createTitledBorder('notifications:')) {
                                checkBox(constraints: CENTER, id: 'viewedChBox', text: 'Email')
                                checkBox(constraints: CENTER, id: 'viewedChBox', text: 'Tray')
                                checkBox(constraints: CENTER, id: 'viewedChBox', text: 'Dialog')
                            }
                            panel(constraints: CENTER) {
                                button('Add', id: 'addBtn')
								button('Update', id: 'updBtn')
                                button('Delete', id: 'delBtn')
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
                    list(id: 'urlsList', model: listModel)
                }
                scrollPane(constraints: CENTER, border: BorderFactory.createTitledBorder('content: ')) {
                    textPane(id: 'fullTxtPane', contentType: 'text/html')
                }
            }
        }
    }
}
