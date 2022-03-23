package africa.talentup.smsappbackend.service.account;

import africa.talentup.smsappbackend.data.model.Account;

public interface AccountService {
    Account findAccountById(Integer id);
}
