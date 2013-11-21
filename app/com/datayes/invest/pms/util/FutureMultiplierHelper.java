package com.datayes.invest.pms.util;


public class FutureMultiplierHelper {
    
    private FutureMultiplierHelper() {
    }
    
    public static int getRatio(String contractMultiplier) {
        int ratio = 0;
        for (int i = 0; i < contractMultiplier.length(); i++) {
            char c = contractMultiplier.charAt(i);
            if (Character.isDigit(c)) {
                ratio = ratio * 10 + (c - '0');
            }
        }
        return ratio;
    }
}
