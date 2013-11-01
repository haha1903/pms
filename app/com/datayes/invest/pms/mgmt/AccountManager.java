package com.datayes.invest.pms.mgmt;

import javax.inject.Inject;

public class AccountManager {

    @Inject
    private AccountDeleteHelper accountDeleteHelper;
    
    public void delete(Long accountId) {
        accountDeleteHelper.deleteAccount(accountId);
    }
}
