package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "SYSTEM_ID_MAPPING")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class SystemIdMapping extends EntityBase  {
    
    private Long pmsId;
    
    private String otherSystemId;
    
    private String idName;
    
    private String otherSystemName;

    @Id
    @Column(name = "PMS_ID")
    public Long getPmsId() {
        return pmsId;
    }

    public void setPmsId(Long pmsId) {
        this.pmsId = pmsId;
    }

    @Column(name = "OTHER_SYSTEM_ID")
    public String getOtherSystemId() {
        return otherSystemId;
    }

    public void setOtherSystemId(String otherSystemId) {
        this.otherSystemId = otherSystemId;
    }

    @Column(name = "ID_NAME")
    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    @Column(name = "OTHER_SYSTEM_NAME")
    public String getOtherSystemName() {
        return otherSystemName;
    }

    public void setOtherSystemName(String otherSystemName) {
        this.otherSystemName = otherSystemName;
    } 
}
