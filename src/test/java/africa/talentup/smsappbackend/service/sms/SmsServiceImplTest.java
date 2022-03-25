package africa.talentup.smsappbackend.service.sms;

import africa.talentup.smsappbackend.data.dto.SmsDto;
import africa.talentup.smsappbackend.data.model.Account;
import africa.talentup.smsappbackend.data.model.PhoneNumber;
import africa.talentup.smsappbackend.data.repository.AccountRepository;
import africa.talentup.smsappbackend.data.repository.PhoneNumberRepository;
import africa.talentup.smsappbackend.web.exception.SmsAppException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class SmsServiceImplTest {

    private final SmsService smsService;
    private final AccountRepository accountRepository;
    private final PhoneNumberRepository phoneNumberRepository;
    private final RedisTemplate<String, String> redisTemplate;


    private final Gson gson = new Gson();
    SmsDto firstRequest;
    SmsDto secondRequest;

    @Autowired
    public SmsServiceImplTest(SmsService smsService, AccountRepository accountRepository,
                              PhoneNumberRepository phoneNumberRepository, RedisTemplate<String, String> redisTemplate) {
        this.smsService = smsService;
        this.accountRepository = accountRepository;
        this.phoneNumberRepository = phoneNumberRepository;
        this.redisTemplate = redisTemplate;
    }

    @BeforeEach
    void setUp() {
        phoneNumberRepository.deleteAll();

        Account firstAccount = accountRepository.save(
                new Account(1, "QWERDF", "User1"));

        Account secondAccount = accountRepository.save(
                new Account(2, "POIUYT", "User2"));

        PhoneNumber firstPhoneNumber = phoneNumberRepository.save(
                new PhoneNumber(1, "6475765856", firstAccount));

        PhoneNumber secondPhoneNumber = phoneNumberRepository.save(
                new PhoneNumber(2, "7576546567", secondAccount));

        PhoneNumber thirdPhoneNumber = phoneNumberRepository.save(
                new PhoneNumber(3, "112200339", secondAccount));

        PhoneNumber fourthPhoneNumber = phoneNumberRepository.save(
                new PhoneNumber(4, "0989876783", secondAccount));

        firstRequest = new SmsDto();
        firstRequest.setSmsSender("6475765856");
        firstRequest.setSmsReceiver("7576546567");
        firstRequest.setSmsBody("Hello, beautiful");

        secondRequest = new SmsDto();
        secondRequest.setSmsSender("112200339");
        secondRequest.setSmsReceiver("0989876783");
        secondRequest.setSmsBody("Handsome!");
    }

    @Test
    @DisplayName("Test that sms can be sent")
    void testThatPhoneNumberCanSendMessage() throws IOException {
        String json = gson.toJson(secondRequest);

        SmsDto response = smsService.sendMessage(json);

        assertThat(response).isNotNull();
        assertThat(response.getSmsBody()).isEqualTo("Handsome!");
        assertThat(response.getSmsReceiver()).isEqualTo("0989876783");
        assertThat(response.getSmsSender()).isEqualTo("112200339");
    }

    @Test
    @DisplayName("Test that api call cannot be more than 50 in 24hrs")
    void testThatRateLimiterWorksOnApiCalls() throws IOException {
        SmsDto request = new SmsDto();
        request.setSmsSender("7576546567");
        request.setSmsReceiver("0989876783");
        request.setSmsBody("Bro, how far?");

        String json = gson.toJson(request);
        for (int apiCall = 1; apiCall <= 50; apiCall++) {
            smsService.sendMessage(json);
        }
        assertThrows(SmsAppException.class, () -> smsService.sendMessage(json));
    }

    @Test
    void cacheMessageThatContainsStopTest() {
        SmsDto request = new SmsDto();
        request.setSmsSender("112200339");
        request.setSmsReceiver("0989876783");
        request.setSmsBody("Heyo! STOP");


        StringBuffer sb = new StringBuffer();
        Set<byte[]> keys = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().keys("*".getBytes());
        assert keys != null;

        for (byte[] data : keys) {
            sb.append(new String(data, 0, data.length));
        }
        log.info("Keys -> ", sb);
        System.out.println(sb);
    }
}