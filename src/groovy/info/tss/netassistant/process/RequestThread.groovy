package info.tss.netassistant.process

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.tss.netassistant.store.SqLiteManager;
import info.tss.netassistant.store.structure.WebChange;

/*
 * Run thread to make one HTTP request.
 */
class RequestThread implements Runnable {
	private WebChange wch
	private static Logger log = LoggerFactory.getLogger(NetFilter.class);
	
	public RequestThread(WebChange wch) {
		this.wch = wch;
	}

	public void run() {
		def attempts = 0;
		try {
			while (!NetFilter.makelRequest(wch) && ++attempts < NetFilter.REQUEST_REPEATS_ON_ERRORS);
			SqLiteManager.SL.createOrUpdateWChange(wch);
		} catch(Exception e){
			log.error("Exception during request $wch.url : ", e);
		}
	}	
	
	public static Thread startFor(WebChange wch){
		if ((new Date().time - wch.last_check) > wch.check_period) {
			RequestThread rt = new RequestThread(wch);
			Thread thread = new Thread(rt);
			thread.start();
			return thread;
		}
		return null;
	}

}
