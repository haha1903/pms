package com.datayes.invest.pms.web.model.fastjson.asset;

public abstract class AssetNode {

    public final AssetNodeType type;

    public final String id;

    public final String name;

    public String marketValue = "";

    public String weight = "";

    public String holdingValue = "";

    public String dailyPnL = "";

    public String floatPnL = "";

    protected AssetNode(AssetNodeType type, String id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }
}
