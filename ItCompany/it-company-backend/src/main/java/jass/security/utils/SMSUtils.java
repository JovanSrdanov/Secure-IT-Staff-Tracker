package jass.security.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jass.security.dto.SMSDto;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;

public class SMSUtils {
    //Twilio
    @Value("${twilioAccountSid}")
    private static String TWILIO_ACCOUNT_SID;
    @Value("${twilioAuthToken}")
    private static String TWILIO_AUTH_TOKEN;
    @Value("${twilioPhoneNumber}")
    private static String TWILIO_PHONE_NUMBER;

    public static void sendSMS(Logger logger, SMSDto smsDto) {
        Twilio.init(smsDto.getTWILIO_ACCOUNT_SID(), smsDto.getTWILIO_AUTH_TOKEN());
        Message.creator(new PhoneNumber(smsDto.getPhoneNumber()),
                        new PhoneNumber(smsDto.getTWILIO_PHONE_NUMBER()), smsDto.getMessage()).create();

        logger.info("SMS sent successfully to a recipient with a phone number: " + smsDto.getPhoneNumber());
    }
}
