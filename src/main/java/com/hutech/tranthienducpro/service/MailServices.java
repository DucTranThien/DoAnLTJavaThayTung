package com.hutech.tranthienducpro.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class MailServices extends SimpleMailMessage {
    @Autowired
    private JavaMailSender emailSender;

    public void SendMail(String to, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Quen Mat Khau");
        message.setText("<a href='"+text+"'>click vao day de doi pass</a>");
        emailSender.send(message);
    }
}
