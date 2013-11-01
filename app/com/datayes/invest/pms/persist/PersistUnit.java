package com.datayes.invest.pms.persist;

public enum PersistUnit {
    
    ACCOUNT_MASTER("accountmaster", false),
    SECURITY_MASTER("securitymaster", true);
    
    private String name;
    
    private boolean isReadOnly;
    
    private PersistUnit(String name, boolean isReadOnly) {
        this.name = name;
        this.isReadOnly = isReadOnly;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isReadOnly() {
        return isReadOnly;
    }
}
