package africa.talentup.smsappbackend.data.repository;

import africa.talentup.smsappbackend.data.model.Account;
import africa.talentup.smsappbackend.data.model.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PhoneNumberRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PhoneNumberRepository phoneNumberRepository;

    Account savedAccount;
    PhoneNumber savedPhoneNumber;


    @BeforeEach
    void setUp() {
        phoneNumberRepository.deleteAll();

        Account account = Account.builder()
                .authId("QWPLASAS")
                .username("User1")
                .build();
        savedAccount = accountRepository.save(account);

        PhoneNumber phonenumber = PhoneNumber.builder()
                .number("08137271515")
                .phoneNumberOwner(savedAccount)
                .build();
        savedPhoneNumber = phoneNumberRepository.save(phonenumber);
    }

    @Test
    @DisplayName("Test that phoneNumber can be saved")
    void savePhoneNumber_test() {
        PhoneNumber anotherPhoneNumber = PhoneNumber.builder()
                .number("08033839572")
                .phoneNumberOwner(savedAccount)
                .build();
        PhoneNumber newlySavedPhoneNumber = phoneNumberRepository.save(anotherPhoneNumber);

        assertThat(newlySavedPhoneNumber).isNotNull();
        assertThat(newlySavedPhoneNumber.getPhoneNumberOwner().getUsername()).isEqualTo("User1");
        assertThat(phoneNumberRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test that saved phoneNumber can be found by number")
    void findPhoneNumberByNumber() {
        Optional<PhoneNumber> foundPhoneNumber = phoneNumberRepository.findByNumber(savedPhoneNumber.getNumber());
        assert foundPhoneNumber.isPresent();

        assertThat(foundPhoneNumber.get().getId()).isEqualTo(savedPhoneNumber.getId());
        assertThat(foundPhoneNumber.get().getPhoneNumberOwner().getUsername()).isEqualTo("User1");
        assertThat(foundPhoneNumber.get().getId()).isEqualTo(savedPhoneNumber.getId());
    }

    @Test
    @DisplayName("Test that saved phoneNumber can be found by id")
    void findPhoneNumberById() {
        Optional<PhoneNumber> foundPhoneNumber = phoneNumberRepository.findById(savedPhoneNumber.getId());
        assert foundPhoneNumber.isPresent();

        assertThat(foundPhoneNumber.get().getId()).isEqualTo(savedPhoneNumber.getId());
        assertThat(foundPhoneNumber.get().getPhoneNumberOwner().getUsername()).isEqualTo("User1");
        assertThat(foundPhoneNumber.get().getId()).isEqualTo(savedPhoneNumber.getId());
    }

    @Test
    @DisplayName("Test that saved phone number can be removed from database")
    void deleteSavedPhoneNumberFromDatabase() {
        PhoneNumber phoneNumber = PhoneNumber.builder()
                .number("08033839572")
                .phoneNumberOwner(savedAccount)
                .build();
        PhoneNumber newlySavedPhoneNumber = phoneNumberRepository.save(phoneNumber);

        assertThat(phoneNumberRepository.findAll().size()).isEqualTo(2);

        phoneNumberRepository.deleteById(newlySavedPhoneNumber.getId());
        assertThat(phoneNumberRepository.findAll().size()).isEqualTo(1);
    }
}