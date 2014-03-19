package info.tss.netassistant.notify

import info.tss.netassistant.AppProps
import info.tss.netassistant.store.structure.WebChange

/**
 * Notify about changes by registered active channels.
 *
 * Author: TsS
 * Date: 12/25/13
 */
class ChangesNotifier {
    static List<NotificationChannel> channels = new ArrayList<NotificationChannel>();
    static {
        if (Boolean.valueOf(AppProps.get("emailEnabled"))) channels.add(new EmailChannel());
        if (Boolean.valueOf(AppProps.get("trayEnabled"))) channels.add(new SystemTrayChannel());
    }

    public static void notifyAllChannels(WebChange wch) {
        for (NotificationChannel ch : channels) {
            if (wch.notifications && wch.notifications.split(",").contains(ch.type)) {
				ch.notify();
			}
        }
    }

}
