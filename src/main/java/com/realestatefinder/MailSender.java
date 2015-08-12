package com.realestatefinder;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender {

    private static final String username = "cjcr.alxandru@gmail.com";
    private static final String password = "qbdeltqnjenwhtfi";

    private static Session session;

    static {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

    }

    public static void sendMail(String subject, String text) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("RealEstateFinder@realestate.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("cjcr_alexandru@yahoo.com"));
            message.setSubject(subject);
            message.setContent(text, "text/html; charset=utf-8");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
