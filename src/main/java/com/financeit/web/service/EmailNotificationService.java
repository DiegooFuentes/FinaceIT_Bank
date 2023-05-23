package com.financeit.web.service;

import com.financeit.web.repositories.PendingTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailNotificationService {

    private final PendingTransactionRepository pendingTransactionRepository;

    public EmailNotificationService(PendingTransactionRepository pendingTransactionRepository) {
        this.pendingTransactionRepository = pendingTransactionRepository;
    }

    public void sendNotification(String receiverEmail) {
        // Sender's email address
        String from = "finaceit.bank@gmail.com";

        // Sender's email password or application-specific password
        String password = "buofezoyarovgebt";

        // Setup properties for the SMTP server
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Get the Session object
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(from, password);
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

            // Set Subject: header field
            message.setSubject("New transaction in your account");

            // Set the actual message
            message.setText("To approve your transaction enter the following code: " + pendingTransactionRepository.findByEmail(receiverEmail).getPasswordTOTP() );

            // Send the message
            Transport.send(message);

            //System.out.println("Notification sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}