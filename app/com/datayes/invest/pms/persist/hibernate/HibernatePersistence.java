package com.datayes.invest.pms.persist.hibernate;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.hibernate.cfg.Configuration;
import org.hibernate.ejb.Ejb3Configuration;

import com.datayes.invest.pms.persist.PersistUnit;
import com.datayes.invest.pms.persist.hibernate.usertype.BigDecimalType;
import com.datayes.invest.pms.persist.hibernate.usertype.BigDecimalTypeDescriptor;
import com.datayes.invest.pms.persist.hibernate.usertype.LocalDateTimeType;
import com.datayes.invest.pms.persist.hibernate.usertype.LocalDateTimeTypeDescriptor;
import com.datayes.invest.pms.persist.hibernate.usertype.LocalDateType;
import com.datayes.invest.pms.persist.hibernate.usertype.LocalDateTypeDescriptor;

public class HibernatePersistence {

    private HibernatePersistence() {
    }
    
    static EntityManagerFactory createEntityManagerFactory(PersistUnit unit) {
        
        Map<String, String> properties = HibernateConfigurator.getProperties(unit.getName());
        Ejb3Configuration cfg = new Ejb3Configuration();
        
        registerUserTypes(cfg.getHibernateConfiguration());
        
        cfg = cfg.configure("persistUnit", properties);
        if (cfg == null) {
            return null;
        }
        EntityManagerFactory emf = cfg.buildEntityManagerFactory();
        
        return emf;
    }
    
    private static void registerUserTypes(Configuration cfg) {
        cfg.registerTypeOverride(new BigDecimalType(new BigDecimalTypeDescriptor()));
        cfg.registerTypeOverride(new LocalDateType(new LocalDateTypeDescriptor()));
        cfg.registerTypeOverride(new LocalDateTimeType(new LocalDateTimeTypeDescriptor()));
    }
}
