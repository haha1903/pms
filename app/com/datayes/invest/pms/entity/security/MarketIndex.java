package com.datayes.invest.pms.entity.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "market_index")
@Proxy(lazy = false)
@SuppressWarnings("unused")
public class MarketIndex extends EntityBase {

    private Long id;

    private String name;

    private MarketIndex() {
        // used by persistence library
    }

    @Id
    @Column(name = "MARKET_INDEX_ID")
    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @Column(name = "MARKET_INDEX_NAME")
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }
}
