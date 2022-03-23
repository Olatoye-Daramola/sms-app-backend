package africa.talentup.smsappbackend.service.sms;

import africa.talentup.smsappbackend.data.dto.SmsDto;
import africa.talentup.smsappbackend.data.model.PhoneNumber;
import africa.talentup.smsappbackend.service.phoneNumber.PhoneNumberService;
import africa.talentup.smsappbackend.service.redis.RedisRateLimiter;
import africa.talentup.smsappbackend.web.exception.SmsAppException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class SmsServiceImpl implements SmsService {

    private final PhoneNumberService phoneNumberService;
    private final RedisRateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    @Autowired
    SmsServiceImpl(PhoneNumberService phoneNumberService, RedisRateLimiter rateLimiter, ObjectMapper objectMapper) {
        this.phoneNumberService = phoneNumberService;
        this.rateLimiter = rateLimiter;
        this.objectMapper = objectMapper;
    }

    @Override
    public SmsDto sendMessage(JSONObject jsonObject) {
        if (jsonObject == null) throw new SmsAppException("JsonObject cannot be null");
        SmsDto smsDto = objectMapper.convertValue(jsonObject, SmsDto.class);

        PhoneNumber foundSender = phoneNumberService.findPhoneNumberByNumber(smsDto.getSmsSender());
        PhoneNumber foundReceiver = phoneNumberService.findPhoneNumberByNumber(smsDto.getSmsReceiver());

        boolean isAllowed = rateLimiter.isAllowed(foundSender.getNumber());

        SmsDto responseDto = new SmsDto();
        if (isAllowed) {
            responseDto.setSmsSender(foundSender.getNumber());
            responseDto.setSmsReceiver(foundReceiver.getNumber());
            responseDto.setSmsBody(smsDto.getSmsBody());
        }
        return responseDto;
    }
}
