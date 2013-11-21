package test.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestGson {

    public static void main(String[] args) {
        Tree root = new Tree();
        root.setId(1);
        root.setName("root");
        Tree secondLevel = new Tree();
        secondLevel.setId(2);
        secondLevel.setName("2nd level");
        Security leaf = new Security();
        leaf.setId(3);
        leaf.setIndustry("Finance");
        leaf.setName("MS");
        leaf.setSymbol("100021");
        
        secondLevel.getChildren().add(leaf);
        root.getChildren().add(secondLevel);
        
        for (int i = 0; i < 5; i++) {
            Security lf = new Security();
            lf.setId(i);
            lf.setIndustry("Finance");
            lf.setName("MS");
            lf.setSymbol("10002" + i);
            secondLevel.getChildren().add(lf);
        }
        
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting().setExclusionStrategies(new GsonExcludeStrategy());
        Gson gson = builder.create();
        long start = System.currentTimeMillis();
        String json = gson.toJson(root);
        long duration = System.currentTimeMillis() - start;
        System.out.println(json);
        System.out.println("duration = " + duration);
    }

}
