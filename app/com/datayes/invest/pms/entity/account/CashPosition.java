package com.datayes.invest.pms.entity.account;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDateTime;

@Entity
@Table(name = "POSITION")
@DiscriminatorValue("CASH")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CashPosition extends Position {

    private CashPosition() {
        // used by hibernate
    }
    
    public CashPosition(Long _accountId, String _positionClassCode, Long _ledgerId, String _exchangeCode,
            String _currencyCode, LocalDateTime _openDate, String _positionStatus,
            LocalDateTime _statusChangeDate) {

        super(_accountId, _positionClassCode, _ledgerId, _exchangeCode, _currencyCode, _openDate,
            _positionStatus, _statusChangeDate);
    }
}
