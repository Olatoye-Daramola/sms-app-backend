package africa.talentup.smsappbackend.service.phoneNumber;

import africa.talentup.smsappbackend.data.model.PhoneNumber;
import africa.talentup.smsappbackend.data.repository.PhoneNumberRepository;
import africa.talentup.smsappbackend.web.exception.SmsAppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneNumberServiceImpl implements PhoneNumberService {

    private final PhoneNumberRepository phoneNumberRepository;

    @Autowired
    PhoneNumberServiceImpl(PhoneNumberRepository phoneNumberRepository) {
        this.phoneNumberRepository = phoneNumberRepository;
    }

    @Override
    public PhoneNumber findPhoneNumberByNumber(String phoneNumber) {
        return phoneNumberRepository.findByNumber(phoneNumber).orElseThrow(
                ()-> new SmsAppException("PhoneNumber " + phoneNumber + " not found")
        );
    }
}
