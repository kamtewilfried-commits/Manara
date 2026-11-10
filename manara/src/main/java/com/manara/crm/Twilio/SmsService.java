package com.manara.crm.Twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.from.phone}")
    private String twilioPhoneNumber;

    public void sendOtp(String phoneNumber, String otp) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required to send OTP.");
        }
        Twilio.init(accountSid, authToken);

        String formattedPhone = phoneNumber.startsWith("+") ? phoneNumber : "+" + phoneNumber;

        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(formattedPhone),
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                "Your OTP code is: " + otp
        ).create();

        System.out.println("SMS envoyé. SID = " + message.getSid());
    }
}