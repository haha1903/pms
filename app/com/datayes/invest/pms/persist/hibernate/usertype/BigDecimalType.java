package com.datayes.invest.pms.persist.hibernate.usertype;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.NumericTypeDescriptor;

import scala.math.BigDecimal;

@SuppressWarnings("serial")
public class BigDecimalType extends AbstractSingleColumnStandardBasicType<scala.math.BigDecimal> {
    public BigDecimalType(JavaTypeDescriptor<BigDecimal> BigDecimalTypeDescriptor) {
        super(NumericTypeDescriptor.INSTANCE, BigDecimalTypeDescriptor);
    }
    
    public String getName() {
        return "scala_big_decimal";
    }

    @Override 
    public boolean registerUnderJavaType() {
        return true;
    }
}
