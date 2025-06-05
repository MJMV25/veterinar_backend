package com.example.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.mail.from:noreply@veterinaria.com}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean emailEnabled;

    public boolean sendEmail(String to, String subject, String message) {
        if (!emailEnabled) {
            logger.info("Email sending is disabled. Would send to: {} with subject: {}", to, subject);
            return true;
        }

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
            logger.info("Email sent successfully to: {}", to);
            return true;

        } catch (MailException e) {
            logger.error("Failed to send email to {}: ", to, e);
            return false;
        }
    }

    public boolean sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (!emailEnabled) {
            logger.info("Email sending is disabled. Would send HTML email to: {} with subject: {}", to, subject);
            return true;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("HTML email sent successfully to: {}", to);
            return true;

        } catch (MessagingException | MailException e) {
            logger.error("Failed to send HTML email to {}: ", to, e);
            return false;
        }
    }

    public boolean sendEmailWithAttachment(String to, String subject, String message, String attachmentPath) {
        if (!emailEnabled) {
            logger.info("Email sending is disabled. Would send email with attachment to: {}", to);
            return true;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message);

            if (attachmentPath != null && !attachmentPath.isEmpty()) {
                helper.addAttachment("attachment", new java.io.File(attachmentPath));
            }

            mailSender.send(mimeMessage);
            logger.info("Email with attachment sent successfully to: {}", to);
            return true;

        } catch (MessagingException | MailException e) {
            logger.error("Failed to send email with attachment to {}: ", to, e);
            return false;
        }
    }
}