package com.datayes.invest.pms.web.model.fastjson.asset;

import java.math.BigDecimal;

public class Asset extends AssetNode {

    public final String code;

    public String marketPrice = "";

    public String priceChange = "";

    public Long holdingQuantity = 0L;

    public String holdingValuePrice = "";

    public String interest = "";

    public String earnedPnL = "";

    public String benchmarkIndexWeight = "";

    public Asset(String name, String code, Long securityId) {
        super(AssetNodeType.leaf, String.valueOf(securityId), name);
        this.code = code;
    }
}
