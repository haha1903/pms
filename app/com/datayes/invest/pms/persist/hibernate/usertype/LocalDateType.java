package com.datayes.invest.pms.persist.hibernate.usertype;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.DateTypeDescriptor;
import org.joda.time.LocalDate;

public class LocalDateType extends AbstractSingleColumnStandardBasicType<org.joda.time.LocalDate> {
    
    public LocalDateType(JavaTypeDescriptor<LocalDate> LocalDateTypeDescriptor) {
        super(DateTypeDescriptor.INSTANCE, LocalDateTypeDescriptor);
    }
    
    public String getName() {
        return "joda_localdate";
    }
    
    @Override
    public boolean registerUnderJavaType() {
        return true;
    }
}
