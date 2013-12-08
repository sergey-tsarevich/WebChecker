package info.tss.netassistant.store

import info.tss.netassistant.store.structure.WebChange
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Manage sqLite operation and functions.
 * Date: 21.01.13
 */
public class SqLiteManager {
    public static final String WEB_CHANGE_TABLE = "WEB_CHANGE";

    public static final SqLiteManager SL = new SqLiteManager();

    private final Logger log = LoggerFactory.getLogger(SqLiteManager.class);
    def sql;

    private SqLiteManager() {
        this.sql = DbInitializer.DI.sql;
    }

    def List<WebChange> getAllWebChanges(){
        List<WebChange> result = [];
        sql.eachRow("SELECT * FROM WEB_CHANGE") {
            result << new WebChange( it.toRowResult() )
        }

        return result;
    }

    // @return id of inserted or updated row
    def int createOrUpdateWChange(WebChange wCh){
        def query = ""
        def params = [wCh.url, wCh.filter, wCh.last_check, wCh.prev_txt, wCh.curr_txt, wCh.viewed, wCh.added_txt, wCh.deleted_txt, wCh.prev_html, wCh.curr_html]
        if (wCh.id) { // update
            query = "update WEB_CHANGE set url=?, filter=?, last_check=?, prev_txt=?, curr_txt=?, viewed=?, added_txt=?, deleted_txt=?, prev_html=?, curr_html=? where id=?"
            params << wCh.id
            sql.executeUpdate(query, params);
            return wCh.id;
        } else { //insert
            query = "INSERT INTO WEB_CHANGE (url, filter, last_check, prev_txt, curr_txt, viewed, added_txt, deleted_txt, prev_html, curr_html) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            return sql.executeInsert(query, params)[0][0];
        }
    }

    def int updateWChangeViewed(def id, def viewed){
        def query = "update WEB_CHANGE set viewed=?, added_txt='', deleted_txt='' where id=?"
        return sql.executeUpdate(query, [(viewed ? 1:0), id]);
    }

    def deleteWChange(def changeId) {
        return sql.execute("delete from WEB_CHANGE where id=? ", [changeId]);
    }

    def saveWebChangesList(List<WebChange> changes){
        if(!changes) return;
        def startTime = System.currentTimeMillis()
        sql.withBatch("""update $WEB_CHANGE_TABLE set last_check=?, filter=?, prev_txt=?, curr_txt=?, added_txt=?, deleted_txt=?, viewed=?, prev_html=?, curr_html=? where id=? """) { ps ->
            changes.each{wch->
                ps.addBatch([wch.last_check, wch.filter, wch.prev_txt, wch.curr_txt, wch.added_txt, wch.deleted_txt, wch.viewed, wch.prev_html, wch.curr_html, wch.id])
            }
        }

        if (log.isDebugEnabled()) log.debug("saveWebChangesList: Executing time: " + (System.currentTimeMillis() - startTime));
    }

}

