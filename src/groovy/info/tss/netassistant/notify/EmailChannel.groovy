package info.tss.netassistant.notify

import info.tss.netassistant.AppProps
import info.tss.netassistant.store.structure.WebChange
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 *
 * Author: TsS
 * Date: 3/23/13
 */
public class EmailChannel implements NotificationChannel {
    private static Logger log = LoggerFactory.getLogger(EmailChannel.class);
    Properties props = new Properties();
    Authenticator authenticator
    def addresses
	public static String TYPE = "2"; 

    EmailChannel() {
        props.put("mail.smtp.host", AppProps.get("smtpHost"));
        props.put("mail.smtp.socketFactory.port", AppProps.get("smtpPort"));
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        def smtpAuth = Boolean.valueOf(AppProps.get("smtpAuth"))
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.port", AppProps.get("smtpPort"));
        if (smtpAuth) authenticator = new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(AppProps.get("smtpLogin"), AppProps.get("smtpPassword"));
            }
        }
        addresses = InternetAddress.parse(AppProps.get("emailTo"));
    }

    @Override
	public String getType() {
		return TYPE;
	}

	/**
     * Send email notifications
     */
    @Override
    void notify(WebChange w) {
        Session session = authenticator ? Session.getDefaultInstance(props, authenticator) : Session.getDefaultInstance(props);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@no-spam.com"));
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject("Changes of " + w.url);
            def txtMessage = "<b>Added for $w.url : </b><br/><hr/>";
            txtMessage += "<div  style=\"width:800px; margin:0 auto;\">";
            txtMessage += w.added_txt ?: "";
            txtMessage += "</div>";
            message.setContent(txtMessage, "text/html; charset=\"UTF-8\"")

            Transport.send(message);
        } catch (Exception e) {
            log.error("Can not notify by email: ", e);
        }
    }

}
