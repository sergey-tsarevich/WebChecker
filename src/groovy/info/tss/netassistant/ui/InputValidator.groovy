package info.tss.netassistant.ui

/**
 *  Validate UI inputs
 *
 * Author: TsS
 * Date: 11/16/13
 */
class InputValidator {

    static boolean isUrlAvailable(String url){
        url = ViewHelper.autoCompleteUrl(url)
        def available = false;
        if (!url) return available;

        try {
            new URL(url).text;
            available = true;
        } catch (Exception e) { // not available or not accessible
        }

        return available;
    }

}
