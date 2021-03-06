package info.tss.netassistant.notify

import info.tss.netassistant.store.structure.WebChange

/**
 *
 * Author: TsS
 * Date: 12/25/13
 */
public interface NotificationChannel {

    void inform(WebChange w);
	
	/*
	 * Each notification implementation have own code(type)
	 */
	String getType();

}