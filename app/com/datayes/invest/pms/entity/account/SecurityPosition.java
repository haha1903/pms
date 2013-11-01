package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDateTime;

@Entity
@Table(name = "POSITION")
@DiscriminatorValue("SECURITY")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SecurityPosition extends Position {
    
    private Long securityId;

    @SuppressWarnings("unused")
    private SecurityPosition() {
        // used by persistence library
    }
    
    public SecurityPosition(Long _accountId, String _positionClassCode, Long _ledgerId, String _exchangeCode,
            String _currencyCode, LocalDateTime _openDate, String _positionStatus,
            LocalDateTime _statusChangeDate, Long _securityId) {
        super(_accountId, _positionClassCode, _ledgerId, _exchangeCode, _currencyCode, _openDate,
            _positionStatus, _statusChangeDate);

        this.securityId = _securityId;
    }
    
    @Column(name = "SECURITY_ID")
    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

}
