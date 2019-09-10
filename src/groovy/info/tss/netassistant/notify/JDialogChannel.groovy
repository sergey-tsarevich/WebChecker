package info.tss.netassistant.notify

import org.jsoup.Jsoup

import javax.swing.JOptionPane;
import sun.audio.AudioPlayer;

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
    void inform(WebChange w) {
		JOptionPane.showMessageDialog(null, ParseUtils.getLastAdding(w.added_txt), w.url + " updated!", JOptionPane.INFORMATION_MESSAGE, null);
    }

}
