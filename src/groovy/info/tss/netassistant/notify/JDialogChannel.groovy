package info.tss.netassistant.notify

import javax.swing.JOptionPane;

import info.tss.netassistant.store.structure.WebChange;


/**
 *
 * Author: TsS
 * Date: 14/03/2014
 */
class JDialogChannel  implements NotificationChannel {
	public static String TYPE = "3";
	
	@Override
	public String getType() {
		return TYPE;
	}

    @Override
    void notify(WebChange w) {
		JOptionPane.showMessageDialog(null, Jsoup.parse(w.added_txt).text(), w.url + " updated!", JOptionPane.INFORMATION_MESSAGE, null);
    }

}
