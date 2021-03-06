package info.tss.netassistant.run

import info.tss.netassistant.AppProps
import info.tss.netassistant.process.InetViewerJob
import info.tss.netassistant.ui.GuiManager
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Start of application.
 * User: tss
 */
public class Starter {
    private static Logger log = LoggerFactory.getLogger(Starter.class);

    public static void main(String[] args) {
        // init config and other resources
        runJobScheduler();
        GuiManager.buildUI();
    }

    private static void runJobScheduler()  {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();

            JobDetail job = JobBuilder.newJob(InetViewerJob.class).withIdentity(AppProps.INFORMER_JOB_KEY).build();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("cron")
                    .withSchedule(CronScheduleBuilder.cronSchedule(AppProps.getViewerCronTrigger()))
                    .build();

            sched.start();
            sched.scheduleJob(job, cronTrigger);
        } catch (SchedulerException e) {
            log.error("Error during job starting...! ", e);
        }
    }

}
