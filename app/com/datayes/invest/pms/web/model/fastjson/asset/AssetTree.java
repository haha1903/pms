package com.datayes.invest.pms.web.model.fastjson.asset;

import java.util.List;

public class AssetTree extends AssetNode {

    public final List<AssetNode> children;

    public AssetTree(AssetNodeType nodeType, String id, String name, List<AssetNode> children) {
        super(nodeType, id, name);
        this.children = children;
    }
}
