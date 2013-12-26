package info.tss.netassistant

import org.quartz.JobKey

/**
 * Author: tss
 * Date: 22.02.2013
 */
public class AppProps {
    // resources
    public static final JobKey INFORMER_JOB_KEY = new JobKey("adInformer", "jobGroup");
    public static final String CONF_FILE = "config.properties";

    private static Properties props = new Properties();

    public static void initConfig(InputStream is){
        BufferedReader utfProps = new BufferedReader(new InputStreamReader(is,"UTF-8"));
        props.load(utfProps);
    }

    static def get(String porpName){
        return props.getProperty(porpName);
    }

    static String getViewerCronTrigger() {
        return props.viewerCronTrigger
    }

    static Boolean isDropDb() {
        return Boolean.valueOf(props.dropDb)
    }

    static String getDbPath() {
        return props.dbPath
    }

}
