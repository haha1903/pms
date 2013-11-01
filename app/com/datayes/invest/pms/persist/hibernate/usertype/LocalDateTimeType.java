package com.datayes.invest.pms.persist.hibernate.usertype;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.DateTypeDescriptor;
import org.joda.time.LocalDateTime;

public class LocalDateTimeType extends AbstractSingleColumnStandardBasicType<org.joda.time.LocalDateTime> {
    
    public LocalDateTimeType(JavaTypeDescriptor<LocalDateTime> LocalDateTimeTypeDescriptor) {
        super(DateTypeDescriptor.INSTANCE, LocalDateTimeTypeDescriptor);
    }

    public String getName() {
        return "joda_localdatetime";
    }
    
    @Override
    public boolean registerUnderJavaType() {
        return true;
    }
}
