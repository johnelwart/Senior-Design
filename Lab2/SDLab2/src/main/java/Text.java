//package com.text.lab2;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class Text {
    // Find your Account Sid and Token at twilio.com/user/account
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_ACCOUNT_AUTH"); // Creating initialization for texting with Twilio
    public static final String PHONE = System.getenv("TWILIO_ACCOUNT_PHONE");

    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        System.out.println("in sendMessage");
        // To, From
        Message message = Message.creator(new PhoneNumber("+15632759872"),
                new PhoneNumber(PHONE),
                "testing").create();

        System.out.println(message.getSid());
    }
    public static void sendMessage() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        System.out.println("in sendMessage");
        // To, From
        Message message = Message.creator(new PhoneNumber("+15632759872"),
                new PhoneNumber(PHONE),
                "testing").create();

        System.out.println(message.getSid());
    }
}