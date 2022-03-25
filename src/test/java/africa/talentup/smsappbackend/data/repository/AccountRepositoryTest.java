package africa.talentup.smsappbackend.data.repository;

import africa.talentup.smsappbackend.data.model.Account;
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
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    Account firstSavedAccount;


    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();

        Account account = Account.builder()
                .authId("QWPLASAS")
                .username("User1")
                .build();
        firstSavedAccount = accountRepository.save(account);
    }

    @Test
    @DisplayName("Test that account can be created and saved")
    void createAccount_test() {
        Account account = Account.builder()
                .authId("20KJUL)")
                .username("User2")
                .build();
        Account secondSavedAccount = accountRepository.save(account);

        assertThat(secondSavedAccount).isNotNull();
        assertThat(secondSavedAccount.getUsername()).isEqualTo("User2");
        assertThat(accountRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test that saved account can be retrieved from storage")
    void retrieveSavedAccountFromDatabase_test() {
        Optional<Account> foundAccount = accountRepository.findById(firstSavedAccount.getId());
        assert foundAccount.isPresent();

        assertThat(foundAccount.get().getId()).isEqualTo(firstSavedAccount.getId());
        assertThat(foundAccount.get().getAuthId()).isEqualTo(firstSavedAccount.getAuthId());
        assertThat(foundAccount.get().getUsername()).isEqualTo("User1");
    }

    @Test
    @DisplayName("Test that saved account can be deleted")
    void deleteSavedAccountFromDatabase() {
        Account account = Account.builder()
                .authId("20KJUL)")
                .username("User2")
                .build();
        Account secondSavedAccount = accountRepository.save(account);
        assertThat(accountRepository.findById(secondSavedAccount.getId())).isNotNull();

        assertThat(accountRepository.findAll().size()).isEqualTo(2);

        accountRepository.deleteById(firstSavedAccount.getId());
        assertThat(accountRepository.findAll().size()).isEqualTo(1);
    }
}