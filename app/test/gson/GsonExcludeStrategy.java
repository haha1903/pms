package test.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonExcludeStrategy implements ExclusionStrategy {
    
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.getAnnotation(GsonExclude.class) != null;
    }

    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(GsonExclude.class) != null;
    }
}
