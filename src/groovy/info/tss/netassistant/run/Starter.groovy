package info.tss.netassistant.run

import info.tss.netassistant.AppConstants
import info.tss.netassistant.process.InetViewerJob
import info.tss.netassistant.process.NetFilter
import info.tss.netassistant.store.SqLiteManager
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
        new Starter().init();
        // start first run --> todo: move to job
//        new NetFilter(SqLiteManager.SL).filterResources();

        GuiManager.buildUI(SqLiteManager.SL);
    }

    def init(){
        readConfig();
//        runJobScheduler();
    }

    public void readConfig() {
        //read config
        Properties props = new Properties();
        InputStream is = getClass().getClassLoader().getResource("config.properties").openStream();
        BufferedReader utfProps = new BufferedReader(new InputStreamReader(is,"UTF-8"));
        props.load(utfProps);

        AppConstants.initConfig(props);
    }

    private void runJobScheduler()  {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();

            JobDetail job = JobBuilder.newJob(InetViewerJob.class).withIdentity(AppConstants.INFORMER_JOB_KEY).build();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("cron")
                    .withSchedule(CronScheduleBuilder.cronSchedule(AppConstants.getViewerCronTrigger()))
                    .build();

            sched.start();
            sched.scheduleJob(job, cronTrigger);
        } catch (SchedulerException e) {
            log.error("Error during job starting...! ", e);
        }
    }

}
