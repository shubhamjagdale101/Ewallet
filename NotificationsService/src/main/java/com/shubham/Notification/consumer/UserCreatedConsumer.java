package com.shubham.Notification.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shubham.Utils.CommonIdentifier;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class UserCreatedConsumer {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JavaMailSender mailSender;

    @KafkaListener(topics = CommonIdentifier.USER_CREATED, groupId = "notification_group")
    public void sendNotification(String msg) throws JsonProcessingException, MessagingException {
        System.out.println(msg);
        JSONObject jsonObject = objectMapper.readValue(msg, JSONObject.class);

        String name = (String) jsonObject.get(CommonIdentifier.USER_CREATED_USER_NAME);
        String email = (String) jsonObject.get(CommonIdentifier.USER_CREATED_USER_EMAIL);
        String phoneNo = (String) jsonObject.get(CommonIdentifier.USER_CREATED_USER_USER_PhNo);
        String userIdentifier = (String) jsonObject.get(CommonIdentifier.USER_CREATED_USER_USER_IDENTIFIER);
        String userIdentifierValue = (String) jsonObject.get(CommonIdentifier.USER_CREATED_USER_USER_IDENTIFIER_VALUE);

        System.out.println(name + " " + email);

        // Create HTML content for the email body
        String htmlContent = "<html><body>"
                + "<h2>Hello " + name + "</h2>"
                + "<p>Your account has been created successfully.</p>"
                + "<p>Here are your credentials:</p>"
                + "<ul>"
                +   "<li><strong>Email:</strong> " + email + "</li>"
                +   "<li><strong>Registered Phone Number: </strong>" + phoneNo + "</li>"
                +   "<li><strong>" + userIdentifier + ": </strong>" + userIdentifierValue + "</li>"
                + "</ul>"
                + "<img src='" + "https://img.freepik.com/premium-vector/send-money-easily-digital-wallet-application-landing-page-template_106954-554.jpg" + "'>"
                + "</body></html>";

        // Create a SimpleMailMessage with HTML content
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("shubhamjagdalerxl@gmail.com");
        helper.setTo(email);
        helper.setSubject("Account Created Successfully");
        helper.setText(htmlContent, true); // Set HTML content to true

        mailSender.send(message);
    }
}
