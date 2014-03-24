package info.tss.netassistant.ui

import javax.swing.JOptionPane

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
            def rezCode = JOptionPane.showConfirmDialog(null, "Access error to get $url: $e.message \nAdd anyway?", "Bad url?",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (rezCode==0) available = true;
        }

        return available;
    }

}
