import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.net.URI;

public class Twilio {
    // Find your Account SID and Auth Token at twilio.com/console
    // and set the environment variables. See http://twil.io/secure
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");

    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("+15558675310"),
                        new com.twilio.type.PhoneNumber("+15017122661"),
                        "McAvoy or Stewart? These timelines can get so confusing.")
                .setStatusCallback(URI.create("http://postb.in/1234abcd"))
                .create();

        System.out.println(message.getSid());
    }
}