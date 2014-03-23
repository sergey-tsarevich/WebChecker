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
		def newb = [50,1,2,3,4]*1000 as byte[] // 2 seconds of beeep sound
		
		AudioPlayer.player.start( new ByteArrayInputStream( newb ));
		JOptionPane.showMessageDialog(null, Jsoup.parse(w.added_txt)?.text(), w.url + " updated!", JOptionPane.INFORMATION_MESSAGE, null);
    }

}
