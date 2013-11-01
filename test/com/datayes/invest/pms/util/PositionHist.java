package com.datayes.invest.pms.util;

import java.math.BigDecimal;

public class PositionHist {

    private PK pk;
    
    private BigDecimal quantity;
    
    private PositionHist() {
    }
    
    public PositionHist(PK pk, BigDecimal quantity) {
        this.pk = pk;
        this.quantity = quantity;
    }
    
    public PK getPk() {
        return pk;
    }

    public void setPk(PK pk) {
        this.pk = pk;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    // TODO use joda time LocalDate to replace asOfDate
    public static class PK {
        
        private Long positionId;
        
        private String asOfDate;

        private PK() {
        }
        
        public PK(Long positionId, String asOfDate) {
            this.positionId = positionId;
            this.asOfDate = asOfDate;
        }

        public Long getPositionId() {
            return positionId;
        }

        public void setPositionId(Long positionId) {
            this.positionId = positionId;
        }

        public String getAsOfDate() {
            return asOfDate;
        }

        public void setAsOfDate(String asOfDate) {
            this.asOfDate = asOfDate;
        }
    }
}
