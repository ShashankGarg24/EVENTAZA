package com.eventza.Eventza.Service;

import com.eventza.Eventza.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VerificationMailService {

    @Autowired
    MailService mailService;

    public void sendVerificationEmail(User user){
        String subject = "Please verify your registration.";

        String senderName = "EVENTAZA APP";

        String userEmail = user.getEmail();
        String mailContent = "<p>Dear "+user.getName()+", </p>";

        String site = "http://c49ab898a5b4.ngrok.io";


        String verifyUrl = "/api/verify/"+user.getVerificationToken();
        mailContent += "<p>Please click the link below to verify the registration</p>";
        // <a href="">VERIFY</a>
        mailContent += "<a href=\""+site+verifyUrl+"\">VERIFY</a><br>";
        //mailContent += site+verifyUrl+" , Please access this url!";

        mailService.sendMail(userEmail ,subject,senderName,mailContent);
    }
}
