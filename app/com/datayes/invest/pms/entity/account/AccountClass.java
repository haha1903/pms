package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "ACCOUNT_CLASS")
@Proxy(lazy = false) 
public class AccountClass extends EntityBase {
    
    private String accountClassCode;
    
    private String accountClassDesc;
    
    private AccountClass() {
        // used by persistence library   
    }

    @Id
    @Column(name = "ACCOUNT_CLASS_CD", length = 20)
    public String getAccountClassCode() {
        return accountClassCode;
    }

    public void setAccountClassCode(String accountClassCode) {
        this.accountClassCode = accountClassCode;
    }

    @Column(name = "ACCOUNT_CLASS_DESC", length = 200)
    public String getAccountClassDesc() {
        return accountClassDesc;
    }

    public void setAccountClassDesc(String accountClassDesc) {
        this.accountClassDesc = accountClassDesc;
    }
}
