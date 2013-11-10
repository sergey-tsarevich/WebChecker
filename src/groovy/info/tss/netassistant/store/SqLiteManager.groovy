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

    def int createOrUpdateWChange(WebChange webChange){
        def query = ""
        def params = [webChange.url, webChange.filter, webChange.last_check, webChange.prev_txt, webChange.curr_txt, webChange.viewed, webChange.added_txt, webChange.deleted_txt]
        if (webChange.id) { // update
            query = "update WEB_CHANGE set url=?, filter=?, last_check=?, prev_txt=?, curr_txt=?, viewed=?, added_txt=?, deleted_txt=? where id=?"
            params << webChange.id
            return sql.executeUpdate(query, params);
        } else { //insert
            query = "INSERT INTO WEB_CHANGE (url, filter, last_check, prev_txt, curr_txt, viewed, added_txt, deleted_txt) values (?, ?, ?, ?, ?, ?, ?, ?)"
            return sql.executeUpdate(query, params);
        }
    }

    def int updateWChangeViewed(def id, def viewed){
        def query = "update WEB_CHANGE set viewed=? where id=?"
        return sql.executeUpdate(query, [viewed ? 1:0, id]);
    }

    def deleteWChange(def changeId) {
        return sql.execute("delete from WEB_CHANGE where id=? ", [changeId]);
    }

    /*
    def saveAdList(List<Ad> ads){
        if(!ads) return;
        def startTime = System.currentTimeMillis()
        sql.withBatch("""INSERT INTO $WEB_CHANGE_TABLE (resource_id, native_id , description , raw_address, raw_area, rooms_count, floor, wc,
                                                    build_date, raw_price, price, area, address, longitude, latitude, status)
                         values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""") { ps ->
            ads.each{ad->
                ps.addBatch([ad.resource_id, ad.native_id, ad.description, ad.raw_address, ad.raw_area, ad.rooms_count, ad.floor, ad.wc, ad.build_date, ad.raw_price, ad.price, ad.area, ad.address, ad.longitude, ad.latitude, ad.status])
            }
        }

        if (log.isDebugEnabled()) log.debug("saveAdList: Executing time: " + (System.currentTimeMillis() - startTime));
    }
*/

}

