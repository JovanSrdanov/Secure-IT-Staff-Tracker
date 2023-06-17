package jass.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SMSDto {
    private String TWILIO_ACCOUNT_SID;
    private String TWILIO_AUTH_TOKEN;
    private String TWILIO_PHONE_NUMBER;
    private String message;
    private String phoneNumber;
}
