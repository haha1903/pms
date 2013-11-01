package com.datayes.invest.pms.persist.hibernate.usertype;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.joda.time.LocalDateTime;

public class LocalDateTimeTypeDescriptor extends AbstractTypeDescriptor<LocalDateTime> {
    
    public LocalDateTimeTypeDescriptor() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime fromString(String value) {
        return new LocalDateTime(value);
    }

    @Override
    public String toString(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X unwrap(LocalDateTime value, Class<X> wrapType, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (java.sql.Timestamp.class.isAssignableFrom(wrapType)) {
            return (X) (new java.sql.Timestamp(value.toDate().getTime()));
        }
        
        if (java.sql.Date.class.isAssignableFrom(wrapType)) {
            return (X) (new java.sql.Date(value.toDate().getTime()));
        }
        throw unknownUnwrap(wrapType);
    }

    @Override
    public <X> LocalDateTime wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (java.sql.Timestamp.class.isInstance(value)) {
            return new LocalDateTime(value);
        }
        if (java.sql.Date.class.isInstance(value)) {
            return new LocalDateTime(value);
        }
        throw unknownWrap(value.getClass());
    }
}
