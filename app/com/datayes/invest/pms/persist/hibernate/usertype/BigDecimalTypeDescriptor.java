package com.datayes.invest.pms.persist.hibernate.usertype;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

import scala.math.BigDecimal;


public class BigDecimalTypeDescriptor extends AbstractTypeDescriptor<scala.math.BigDecimal> {

    public BigDecimalTypeDescriptor() {
        super(scala.math.BigDecimal.class);
    }

    @Override
    public BigDecimal fromString(String value) {
        return new scala.math.BigDecimal(new java.math.BigDecimal(value));
    }

    @Override
    public String toString(BigDecimal value) {
        if (value == null ) {
            return null;
        }
        return value.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X unwrap(BigDecimal value, Class<X> wrapType, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        else if (java.math.BigDecimal.class.isAssignableFrom(wrapType)) {
           return (X)value.bigDecimal();
        }
        else throw unknownUnwrap(wrapType);
    }

    @Override
    public <X> BigDecimal wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (java.math.BigDecimal.class.isInstance(value)) {
            return new scala.math.BigDecimal((java.math.BigDecimal)value);
        }
        throw unknownWrap(value.getClass());
    }
}
