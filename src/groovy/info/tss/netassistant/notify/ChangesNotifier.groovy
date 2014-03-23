package info.tss.netassistant.notify

import info.tss.netassistant.store.SqLiteManager;
import info.tss.netassistant.store.structure.WebChange

/**
 * Notify about changes by registered active channels.
 *
 * Author: TsS
 * Date: 12/25/13
 */
class ChangesNotifier {
	private List<NotificationChannel> channels = new ArrayList<NotificationChannel>();
	public static final ChangesNotifier I = new ChangesNotifier();
	
	private ChangesNotifier() {
		channels.add(new EmailChannel());
		channels.add(new SystemTrayChannel());
		channels.add(new JDialogChannel());
	}

    public void notifyAllChannels(WebChange wch) {
        for (NotificationChannel ch : channels) {
            if (wch.notifications && wch.notifications.split(",").contains(ch.type)) {
				ch.inform(wch);
			}
        }
    }

}
