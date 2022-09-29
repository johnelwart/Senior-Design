package com.example.lab1;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class Example {
    // Find your Account Sid and Token at twilio.com/user/account
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_ACCOUNT_AUTH");
    public static final String PHONE = System.getenv("TWILIO_ACCOUNT_PHONE");

    public static void main(String args, String customMessage) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // To, From
        Message message = Message.creator(new PhoneNumber("+1" + args),
                new PhoneNumber(PHONE),
                customMessage).create();

        System.out.println(message.getSid());
    }
}