package test.gson;

import java.util.ArrayList;
import java.util.List;

public class Tree extends Node {

    private List<Node> children = new ArrayList<Node>();

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }
}
