package africa.talentup.smsappbackend.service.phoneNumber;

import africa.talentup.smsappbackend.data.model.PhoneNumber;

public interface PhoneNumberService {
    PhoneNumber findPhoneNumberByNumber(String number);
}
