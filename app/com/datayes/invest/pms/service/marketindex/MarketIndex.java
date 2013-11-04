package com.datayes.invest.pms.service.marketindex;


import java.util.Map;

public class MarketIndex {

    private Long id;

    private String name;

    private Map<Long, MarketIndexComponent> components = null;


    public MarketIndex(Long id, String name, Map<Long, MarketIndexComponent> components) {
        this.id = id;
        this.name = name;
        this.components = components;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Long, MarketIndexComponent> getComponents() {
        return components;
    }

    public void setComponents(Map<Long, MarketIndexComponent> components) {
        this.components = components;
    }
}
