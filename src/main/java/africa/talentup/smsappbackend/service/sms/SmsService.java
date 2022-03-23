package africa.talentup.smsappbackend.service.sms;

import africa.talentup.smsappbackend.data.dto.SmsDto;
import org.json.JSONObject;

public interface SmsService {
    SmsDto sendMessage(JSONObject jsonObject);
}
