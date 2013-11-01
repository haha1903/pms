package com.datayes.invest.pms.persist.hibernate.usertype;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.joda.time.LocalDate;

public class LocalDateTypeDescriptor extends AbstractTypeDescriptor<LocalDate> {
    
    public LocalDateTypeDescriptor() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate fromString(String value) {
        return new LocalDate(value);
    }

    @Override
    public String toString(LocalDate value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X unwrap(LocalDate value, Class<X> wrapType, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (java.sql.Date.class.isAssignableFrom(wrapType)) {
            return (X) (new java.sql.Date(value.toDate().getTime()));
        }
        throw unknownUnwrap(wrapType);
    }

    @Override
    public <X> LocalDate wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (java.sql.Date.class.isInstance(value)) {
            return new LocalDate(value);
        }
        throw unknownWrap(value.getClass());
    }
}
