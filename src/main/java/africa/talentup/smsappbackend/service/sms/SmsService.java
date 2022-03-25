package africa.talentup.smsappbackend.service.sms;

import africa.talentup.smsappbackend.data.dto.SmsDto;

import java.io.IOException;

public interface SmsService {
    SmsDto sendMessage(String jsonObject) throws IOException;
    SmsDto receiveMessage(String jsonObject) throws IOException;
}
