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

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {

    private final PhoneNumberService phoneNumberService;
    private final RedisRateLimiter rateLimiter;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    private final Gson gson = new Gson();

    @Autowired
    SmsServiceImpl(PhoneNumberService phoneNumberService, RedisRateLimiter rateLimiter,
                   ObjectMapper objectMapper, RedisTemplate<String, String> redisTemplate) {
        this.phoneNumberService = phoneNumberService;
        this.rateLimiter = rateLimiter;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public SmsDto receiveMessage(String jsonObject) {
        SmsDto smsRequestDto = transformJsonInput(jsonObject);

        validateInput(smsRequestDto);

        SmsDto responseDto = objectMapper.convertValue(smsRequestDto, SmsDto.class);

        checkForStopWordInMessage(responseDto);
        return responseDto;
    }

    @Override
    public SmsDto sendMessage(String jsonObject) {
        SmsDto smsRequestDto = transformJsonInput(jsonObject);

        validateInput(smsRequestDto);
        return validateNumberOfApiCalls(smsRequestDto);
    }




    private SmsDto transformJsonInput(String jsonObject) {
        if (jsonObject == null) throw new SmsAppException("Json cannot be null");
        return gson.fromJson(jsonObject, SmsDto.class);
    }

    private void validateInput(SmsDto smsRequestDto) {
        if(smsRequestDto.getSmsSender() == null) throw new SmsAppException("Sender phone number is missing");
        if(smsRequestDto.getSmsReceiver() == null) throw new SmsAppException("Receiver phone number is missing");
        if(smsRequestDto.getSmsBody() == null) throw new SmsAppException("Message body is missing");

        phoneNumberService.findPhoneNumberByNumber(smsRequestDto.getSmsSender());
        phoneNumberService.findPhoneNumberByNumber(smsRequestDto.getSmsReceiver());
    }

    private void checkForStopWordInMessage(SmsDto responseDto) {
        PhoneNumber foundSender = phoneNumberService.findPhoneNumberByNumber(responseDto.getSmsSender());
        Set<String> wordsContainer = Set.of(responseDto.getSmsBody().split("[ !@#$%^&*()_+}{\":?><,./;'=]"));

        if (wordsContainer.contains("STOP") || wordsContainer.contains("stop")) {
            redisTemplate.opsForHash().put(responseDto.getSmsSender(), foundSender.getId(), responseDto.getSmsReceiver());
            redisTemplate.expire(responseDto.getSmsSender(), 4, TimeUnit.HOURS);
        }
    }

    private SmsDto validateNumberOfApiCalls(SmsDto smsRequestDto) {
        boolean isAllowed = rateLimiter.isAllowed(smsRequestDto.getSmsSender());
        if (isAllowed) {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(smsRequestDto.getSmsSender()))) {
                throw new SmsAppException(
                        "sms from " + smsRequestDto.getSmsSender() + " to " + smsRequestDto.getSmsReceiver() + " blocked by STOP request");
            }
            return objectMapper.convertValue(smsRequestDto, SmsDto.class);
        } else throw new SmsAppException("API call limit exceeded");
    }
}
