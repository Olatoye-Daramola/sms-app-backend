package africa.talentup.smsappbackend.web.controller;

import africa.talentup.smsappbackend.data.dto.SmsDto;
import africa.talentup.smsappbackend.service.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/")
public class SmsController {

    private final SmsService smsService;

    @Autowired
    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }


    @PostMapping("/inbound/sms")
    public ResponseEntity<?> receiveMessage (@RequestBody String jsonObject) {
        try {
            SmsDto response = smsService.receiveMessage(jsonObject);
            return new ResponseEntity<>(response, OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), BAD_REQUEST);

        }
    }

    @PostMapping("/outbound/sms")
    public ResponseEntity<?> sendMessage (@RequestBody String jsonObject) {
        try {
            SmsDto response = smsService.sendMessage(jsonObject);
            return new ResponseEntity<>(response, OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), BAD_REQUEST);

        }
    }
}
