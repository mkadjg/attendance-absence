package com.absence.services;

import com.absence.exceptions.SendEmailException;
import com.absence.payloads.EmailPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.host}")
    String host;

    @Value("${spring.mail.port}")
    String port;

    @Value("${spring.mail.username}")
    String username;

    @Value("${spring.mail.password}")
    String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    String auth;

    @Value("${spring.mail.properties.mail.smtp.connection-timeout}")
    String connectionTimeout;

    @Value("${spring.mail.properties.mail.smtp.timeout}")
    String timeout;

    @Value("${spring.mail.properties.mail.smtp.write-timeout}")
    String writeTimeout;

    @Autowired
    SpringTemplateEngine thymeleafTemplateEngine;

    @Override
    public void sendEmail(EmailPayload emailPayload) throws Exception {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", auth);
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.connectiontimeout", connectionTimeout);
            props.put("mail.smtp.timeout", timeout);
            props.put("mail.smtp.writetimeout", writeTimeout);
            props.put("mail.smtp.starttls.enable", true);

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username, "Attendance 79 Official", StandardCharsets.UTF_8.name()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailPayload.getReceiver()));
            msg.setSubject(emailPayload.getSubject());
            msg.setContent(emailPayload.getEmailBody(), MediaType.TEXT_HTML_VALUE);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SendEmailException("Failed to send email notification!");
        }
    }

    @Override
    public String generateHtmlEmailBody(String htmlFilenamePath, Map<String, Object> variables) {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(variables);
        return thymeleafTemplateEngine.process(htmlFilenamePath, thymeleafContext);
    }


}
