package com.datayes.invest.pms.web.model.fastjson.asset;

import java.math.BigDecimal;

public abstract class AssetNode {

    public final AssetNodeType type;

    public final String id;

    public final String name;

    public BigDecimal marketValue = null;

    public BigDecimal weight = null;

    public BigDecimal holdingValue = null;

    public BigDecimal dailyPnL = null;

    public BigDecimal floatPnL = null;

    protected AssetNode(AssetNodeType type, String id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }
}
