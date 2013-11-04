package com.datayes.invest.pms.service.marketindex;


public class MarketIndexComponent {

    private Long securityId;

    private Double weight;


    public MarketIndexComponent(Long securityId, Double weight) {
        this.securityId = securityId;
        this.weight = weight;
    }

    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
