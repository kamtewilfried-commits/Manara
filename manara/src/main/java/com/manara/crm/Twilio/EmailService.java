package com.manara.crm.Twilio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Code OTP");
        message.setText("Votre code OTP est : " + otp);
        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Vérification de votre compte");
        message.setText(
                "Bonjour,\n\n" +
                        "Cliquez sur ce lien pour vérifier votre email :\n" +
                        verificationLink + "\n\n" +
                        "Si vous n'êtes pas à l'origine de cette demande, ignorez ce message."
        );
        mailSender.send(message);
    }

    public void sendSimple(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}