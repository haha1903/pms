package com.datayes.invest.pms.entity;

import java.io.Serializable;

import com.datayes.invest.pms.util.BeanUtil;

abstract public class EntityBase implements Serializable {

    @Override
    public String toString() {
        return BeanUtil.toString(this);
    }
}
