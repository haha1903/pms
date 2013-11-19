package com.datayes.invest.pms.web.model.fastjson.asset;

import java.math.BigDecimal;

public class Asset extends AssetNode {

    public final String code;

    public BigDecimal marketPrice = null;

    public BigDecimal priceChange = null;

    public Long holdingQuantity = 0L;

    public BigDecimal holdingValuePrice = null;

    public BigDecimal interest = null;

    public BigDecimal earnedPnL = null;

    public BigDecimal benchmarkIndexWeight = null;

    public Asset(String name, String code, Long securityId) {
        super(AssetNodeType.leaf, String.valueOf(securityId), name);
        this.code = code;
    }
}
