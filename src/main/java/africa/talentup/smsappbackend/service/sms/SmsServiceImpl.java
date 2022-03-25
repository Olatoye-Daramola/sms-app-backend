package africa.talentup.smsappbackend.service.sms;

import africa.talentup.smsappbackend.data.dto.SmsDto;
import africa.talentup.smsappbackend.data.model.PhoneNumber;
import africa.talentup.smsappbackend.service.phoneNumber.PhoneNumberService;
import africa.talentup.smsappbackend.service.redis.RedisRateLimiter;
import africa.talentup.smsappbackend.web.exception.SmsAppException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {

    private final PhoneNumberService phoneNumberService;
    private final RedisRateLimiter rateLimiter;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    private final Gson gson = new Gson();

    private Set<String> wordsContainer = new HashSet<>();

    @Autowired
    SmsServiceImpl(PhoneNumberService phoneNumberService, RedisRateLimiter rateLimiter,
                   ObjectMapper objectMapper, RedisTemplate<String, String> redisTemplate) {
        this.phoneNumberService = phoneNumberService;
        this.rateLimiter = rateLimiter;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public SmsDto sendMessage(String jsonObject) {
        if (jsonObject == null) throw new SmsAppException("Json cannot be null");
        SmsDto smsDto = gson.fromJson(jsonObject, SmsDto.class);

        PhoneNumber foundSender = phoneNumberService.findPhoneNumberByNumber(smsDto.getSmsSender());
        PhoneNumber foundReceiver = phoneNumberService.findPhoneNumberByNumber(smsDto.getSmsReceiver());

        boolean isAllowed = rateLimiter.isAllowed(foundSender.getNumber());
        SmsDto responseDto = new SmsDto();
        if (isAllowed) {
            responseDto.setSmsSender(foundSender.getNumber());
            responseDto.setSmsReceiver(foundReceiver.getNumber());
            responseDto.setSmsBody(smsDto.getSmsBody());

            wordsContainer = Set.of(responseDto.getSmsBody().split("[ !@#$%^&*()_+}{\":?><,./;'=]"));
            if (wordsContainer.contains("STOP") || wordsContainer.contains("stop")) {
                redisTemplate.opsForHash().put(responseDto.getSmsSender(), foundSender.getId(), responseDto.getSmsReceiver());
                redisTemplate.expire(responseDto.getSmsSender(), 4, TimeUnit.HOURS);
            }
        } else throw new SmsAppException("API call limit exceeded");
        return responseDto;
    }

    @Override
    public SmsDto receiveMessage(String jsonObject) throws IOException {
        if (jsonObject == null) throw new SmsAppException("Json cannot be null");
        SmsDto smsDto = objectMapper.readValue(jsonObject, SmsDto.class);

        SmsDto responseDto = new SmsDto();
        responseDto.setSmsSender(smsDto.getSmsSender());
        responseDto.setSmsReceiver(smsDto.getSmsReceiver());
        responseDto.setSmsBody(smsDto.getSmsBody());

        return responseDto;
    }
}
