package africa.talentup.smsappbackend.data.repository;

import africa.talentup.smsappbackend.data.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}
