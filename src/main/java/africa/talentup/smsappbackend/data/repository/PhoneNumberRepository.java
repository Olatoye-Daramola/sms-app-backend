package africa.talentup.smsappbackend.data.repository;

import africa.talentup.smsappbackend.data.model.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Integer> {
}
