package info.tss.netassistant.store

import groovy.sql.Sql
import info.tss.netassistant.AppProps
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteConnectionPoolDataSource

import java.nio.file.Paths
import java.sql.BatchUpdateException

/**
 * Database initializer class. Store init db sql script
 * Author: TsS
 * Date: 3/9/13
 */
public class DbInitializer {
    public static final String INIT_SCRIPT = """
        CREATE TABLE WEB_CHANGE (       -- #web changes table
            id  INTEGER PRIMARY KEY,
            url TEXT,                   -- url to check for changes
            filter TEXT,                -- Some filter, todo: think about it!
            last_check DATETIME,        -- time of last check
            prev_txt TEXT,              -- previous text to compare
            prev_html TEXT,              -- previous html to compare
            curr_txt TEXT,              -- current text to compare
            curr_html TEXT,              -- current html to compare
            viewed INTEGER default 0,   -- 0 and 1 - if change(s) was viewed
            added_txt TEXT,             -- difference that was added
    		deleted_txt TEXT,           -- difference that was deleted
    		check_period INTEGER default 86400000, -- check period (default one day)
            notifications TEXT default '1,2', -- comma separated notifications types
			headers TEXT,             	-- extra headers for url
			tag TEXT,             		-- tag to group to
			active integer default 1    -- if row active
        );
    """

    public static final DbInitializer DI = new DbInitializer();
    private static final String DB_NAME = "/data/inter_store"

    private Logger log = LoggerFactory.getLogger(DbInitializer.class);
    private String dbPath;
    def sql;

    private DbInitializer() {
        this.dbPath =  DB_NAME
        if (AppProps.getDbPath()) {
            this.dbPath = AppProps.getDbPath()
        } else { // using default DB
            this.dbPath = Paths.get("").toAbsolutePath().toString() + "/src/dist" + DB_NAME
            log.info("Loading default DB: {}", this.dbPath)
        }
        initConnection();
        if (Boolean.valueOf(AppProps.isDropDb())) this.dropAllTables();

        initDataBase();
    }

    private void initConnection() {
        SQLiteConfig config = new SQLiteConfig();
        config.setSynchronous(SQLiteConfig.SynchronousMode.OFF);

        SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
        def f = new File(dbPath);
        if (!f.exists()) {
            log.error("ERROR_: DB file \"${f.absolutePath}\" doesn't exist!");
        }
        dataSource.setUrl("jdbc:sqlite:$dbPath");
        dataSource.setConfig(config);
        sql = new Sql(dataSource);
    }

    // Run initial script for application
    private void initDataBase() {
        def isExist = sql.firstRow("SELECT name FROM sqlite_master WHERE type='table' AND name=?;", [SqLiteManager.WEB_CHANGE_TABLE]);

        if (!isExist) {
            def updateCounts = 0;
            log.info("Executing init script...")
            try {
                updateCounts = sql.withBatch { stmt ->
                    DbInitializer.INIT_SCRIPT.split(";").each {
                        if (it) stmt.addBatch(it.trim() + ";");  // batch entry 8: [SQLITE_MISUSE]  Library used incorrectly (not an error) ignore!
                    }
                }
            } catch (BatchUpdateException e) {} // It is normal - all works
            log.info("There were updated: $updateCounts rows.")

        } else {
            log.info("Database exist!")
        }
    }

    private void dropAllTables() {
        log.info("Executing drop script...")

        sql.withBatch { stmt ->
            stmt.addBatch("drop table if exists WEB_CHANGE;");
        }
        log.info("All tables were deleted!")
    }

}
