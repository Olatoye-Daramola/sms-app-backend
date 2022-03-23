package africa.talentup.smsappbackend.service.account;

import africa.talentup.smsappbackend.data.model.Account;
import africa.talentup.smsappbackend.data.repository.AccountRepository;
import africa.talentup.smsappbackend.web.exception.SmsAppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account findAccountById(Integer id) {
        return accountRepository.findById(id).orElseThrow(
                ()-> new SmsAppException("Account with " + id + " not found")
        );
    }
}
