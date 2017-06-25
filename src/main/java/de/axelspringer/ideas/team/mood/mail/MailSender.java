package de.axelspringer.ideas.team.mood.mail;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Properties;

public class MailSender {

    private final static Logger log = LoggerFactory.getLogger(MailSender.class);

    private final String username;

    private final String password;

    public MailSender(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void writeToFile(String receiverEmail, String subject, String htmlBody) {
        try {
            FileUtils.writeStringToFile(new File("/Users/swaschni/Projekte/ideas-teammood/target/mail.html"), htmlBody);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void sendViaMailgun(String receiverEmail, String subject, String htmlBody) {
        try {
            log.info("Sending mail with MailGun to: " + receiverEmail);

            Properties props = new Properties();

            props.put("mail.smtp.starttls.enable", true); // added this line
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress("Axel Springer Ideas Engineering <hello@asideas.de>"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");

            log.info("Sending mail...");
            Transport.send(message);
            log.info("Done.");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void send(String receiverEmail, String subject, String htmlBody) {
        try {
            log.info("Sending mail to: " + receiverEmail);

            Properties props = new Properties();

            props.put("mail.smtp.starttls.enable", true); // added this line
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress("Axel Springer Ideas Engineering <hello@asideas.de>"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");

            log.info("Sending mail...");
            Transport.send(message);
            log.info("Done.");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}