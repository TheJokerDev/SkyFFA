package me.thejokerdev.skyffa.utils;

import org.bukkit.block.Sign;

public class SignUtils {

    public static void setSignText(Sign var0, int var1, String var2) {
        byte var3 = 15;
        if (null != var2 && null != var0) {
            if (var1 > 3) {
                throw new IllegalArgumentException("position was > than 3");
            } else {
                if (var2.length() >= var3) {
                    String var4 = var2.substring(0, var3);
                    var0.setLine(var1, var4);
                } else {
                    var0.setLine(var1, var2);
                }

            }
        } else {
            throw new IllegalArgumentException("Sign or Text to set in sign was null");
        }
    }
}
