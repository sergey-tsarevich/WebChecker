package info.tss.netassistant.notify

import info.tss.netassistant.store.structure.WebChange

import javax.swing.*
import java.awt.*

/**
 *
 * Author: TsS
 * Date: 12/25/13
 */
class SystemTrayChannel implements NotificationChannel {

    private TrayIcon trayIcon;
    private Image img = new ImageIcon(this.class.classLoader.getResource('4.png')).image;

    @Override
    void notify(WebChange w) {
        if (!trayIcon) {
            trayIcon = new TrayIcon(image: img, tooltip: w.url, imageAutoSize: true,
                    actionPerformed: {
                        SystemTray.getSystemTray().remove(trayIcon);
                        trayIcon = null
                    })
            SystemTray.getSystemTray().add(trayIcon);
        }
        trayIcon.displayMessage(w.url + " updated!", w.added_txt, TrayIcon.MessageType.INFO)
    }
}
