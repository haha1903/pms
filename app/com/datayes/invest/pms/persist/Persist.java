package com.datayes.invest.pms.persist;

public class Persist {

    private static PersistService persistService;
    
    private Persist() {
    }

    public static void setPersistService(PersistService ps) {
        persistService = ps;
    }
    
    public static Transaction beginTransaction() {
        check();
        Transaction tx = persistService.beginTransaction();
        return tx;
    }
    
    public static Transaction getTransaction() {
        check();
        Transaction tx = persistService.getTransaction();
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
