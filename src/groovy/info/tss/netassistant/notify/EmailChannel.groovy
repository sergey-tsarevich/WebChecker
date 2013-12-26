package info.tss.netassistant.notify

import info.tss.netassistant.AppProps
import info.tss.netassistant.store.structure.WebChange

import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 *
 * Author: TsS
 * Date: 3/23/13
 */
public class EmailChannel implements NotificationChannel {
    Properties props = new Properties();
    Authenticator authenticator
    def addresses

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
            def txtMessage = "<b>Hi!</b> <br/>There are some content was added:<br/>";
            txtMessage += w.added_txt;
            message.setContent(txtMessage, "text/html")

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
