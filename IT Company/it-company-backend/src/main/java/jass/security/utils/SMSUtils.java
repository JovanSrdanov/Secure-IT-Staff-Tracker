package jass.security.utils;

import ClickSend.Api.SmsApi;
import ClickSend.ApiClient;
import ClickSend.Model.SmsMessage;
import ClickSend.Model.SmsMessageCollection;
import jass.security.dto.SMSDto;
import org.slf4j.Logger;

import java.util.List;

public class SMSUtils {
    public static void sendSMS(Logger logger, ApiClient clickSendConfig, SMSDto smsDto) {
        SmsApi smsApi = new SmsApi(clickSendConfig);
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.setSource(smsDto.getSource());
        smsMessage.setBody(smsDto.getBody());
        smsMessage.setTo(smsDto.getRecipient());

        SmsMessageCollection smsCollection = new SmsMessageCollection();
        smsCollection.setMessages(List.of(smsMessage));

        try {
            //smsApi.smsSendPost(smsCollection);
            logger.info("SMS sent successfully");
        }catch (Exception e) {
            logger.error("Error while trying to send an sms with Clicksend API");
        }
    }
}
