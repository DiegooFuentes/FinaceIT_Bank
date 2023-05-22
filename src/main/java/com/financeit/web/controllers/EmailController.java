package com.financeit.web.controllers;

import com.financeit.web.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmailController {


    @Autowired
    private EmailNotificationService emailNotificationService;

    @GetMapping("/send-notification")// works
    public void sendNotification() {
        // Get the necessary information from the request
        String to = "example@gmail.com";
        String subject = "New transfer in your account";
        String message = "To authorize your transfer enter the following code: ";

        // Send the email notification
        emailNotificationService.sendNotification(to, subject, message);
    }

}
