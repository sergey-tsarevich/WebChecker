package info.tss.netassistant.process

import info.tss.netassistant.ui.GuiManager
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * Author: TsS
 * Date: 4/5/13
 */
public class InetViewerJob implements Job {
    private Logger log = LoggerFactory.getLogger(InetViewerJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting job...");

        if( NetFilter.requestNotifyAndSave( GuiManager.listModel.iterator().toList(), false) )
            GuiManager.refreshUrlList();

        log.info("Ending job...");
    }
}
