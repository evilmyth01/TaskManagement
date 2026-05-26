package com.notificationService.notificationService.services;

import com.notificationService.notificationService.dtos.TaskEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {

    JavaMailSender mailSender;

    public void send(String assignedTo, String assignedBy, String sub, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(assignedTo);
            msg.setSubject(sub);
            msg.setText(body);

            mailSender.send(msg);
            log.info("Successfully send the mail to "+assignedTo);
        } catch (Exception e) {
            log.error("Error occured while sending mail "+e.getMessage());
            throw new RuntimeException("error sending message");
        }
    }
}
