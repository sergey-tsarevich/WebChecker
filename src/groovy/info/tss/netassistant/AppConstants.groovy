package info.tss.netassistant

import org.quartz.JobKey

/**
 * Author: tss
 * Date: 22.02.2013
 */
public class AppConstants {
    // resources
    public static final String APP_ROOT_PATH = "APP_ROOT_PATH";
    public static final String PAGE_NUMBER_SIGN = "@";
    public static final JobKey INFORMER_JOB_KEY = new JobKey("adInformer", "jobGroup");

    public static void initConfig(Properties props){
        this.dropDb = Boolean.valueOf(props.dropDb);
        this.viewerCronTrigger = props.viewerCronTrigger;
        this.dbPath = props.dbPath;
    }

    private static Boolean dropDb = Boolean.FALSE;
    private static String viewerCronTrigger;
    private static String dbPath;

    static String getViewerCronTrigger() {
        return viewerCronTrigger
    }

    static Boolean isDropDb() {
        return dropDb
    }

    static String getDbPath() {
        return dbPath
    }

}
