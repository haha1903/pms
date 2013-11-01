package com.datayes.invest.pms.persist;

public class Persist {

    private static PersistService persistService;
    
    private Persist() {
    }

    public static void setPersistService(PersistService ps) {
        persistService = ps;
    }
    
    public static Transaction beginTransaction() {
        return beginTransaction(PersistUnit.ACCOUNT_MASTER);
    }
    
    public static Transaction beginTransaction(PersistUnit unit) {
        check();
        Transaction tx = persistService.beginTransaction(unit);
        return tx;
    }
    
    public static Transaction currentTransaction() {
        check();
        Transaction tx = persistService.currentTransaction();
        return tx;
    }
    
    private static void check() {
        if (persistService == null) {
            throw new PersistException("persistService is null");
        }
    }
}
