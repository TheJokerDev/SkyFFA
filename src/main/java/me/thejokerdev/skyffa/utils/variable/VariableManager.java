package me.thejokerdev.skyffa.utils.variable;

import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.skyffa.player.SkyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VariableManager {
    private final Map<Integer, VariableReplacer> replacers = new HashMap();
    private final List<RegisteredVariable> variables = Collections.synchronizedList(new ArrayList());

    public VariableManager() {
    }

    private void registerMethod(Method var1, int var2, Variable var3) {
        this.variables.add(new RegisteredVariable(var1, var3, var2));
    }

    public void registerVariableReplacer(VariableReplacer var1) {
        int var2 = this.replacers.size();
        this.replacers.put(var2, var1);
        Method[] var3 = var1.getClass().getDeclaredMethods();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Method var6 = var3[var5];
            if (var6.isAnnotationPresent(Variable.class)) {
                Variable var7 = null;
                Annotation[] var8 = var6.getDeclaredAnnotations();
                int var9 = var8.length;

                for(int var10 = 0; var10 < var9; ++var10) {
                    Annotation var11 = var8[var10];
                    if (var11 instanceof Variable) {
                        var7 = (Variable)var11;
                        break;
                    }
                }

                if (var7 != null) {
                    Class[] var12 = var6.getParameterTypes();
                    if (var12.length == 1 || var12[0].equals(Player.class)) {
                        this.registerMethod(var6, var2, var7);
                    }
                }
            }
        }

    }

    private Pattern getVariablePattern(String var1) {
        return Pattern.compile("[%](?i)" + var1 + "[:]\\d+[,]?(\\d+)?[%]");
    }

    public List<String> replaceList(SkyPlayer var1, List<String> var2) {
        ArrayList var3 = new ArrayList();
        Iterator var4 = var2.iterator();

        while(var4.hasNext()) {
            String var5 = (String)var4.next();
            this.replaceText(var1, var5);
            var3.add(var5);
        }

        return var3;
    }

    public String replaceText(SkyPlayer var1, String var2) {
        Iterator var3 = this.variables.iterator();

        while(var3.hasNext()) {
            RegisteredVariable var4 = (RegisteredVariable)var3.next();
            String[] var5 = var4.getVariable().vars();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String var8 = var5[var7];
                Matcher var9 = this.getVariablePattern(var8).matcher(var2);
                int[] var10 = new int[]{-1, -1};
                boolean var11 = false;
                if (var9.find()) {
                    var11 = true;
                    String[] var12 = var2.substring(var9.start(), var9.end()).split(":");
                    if (var12[1].contains(",")) {
                        String[] var13 = var12[1].split(",", 2);

                        try {
                            var10[0] = Integer.parseInt(var13[0]);
                        } catch (NumberFormatException var16) {
                        }

                        try {
                            var10[1] = Integer.parseInt(var13[1].substring(0, var13[1].length() - 1));
                        } catch (NumberFormatException var15) {
                        }
                    } else {
                        try {
                            var10[1] = Integer.parseInt(var12[1].substring(0, var12[1].length() - 1));
                        } catch (NumberFormatException var17) {
                        }
                    }
                } else if (!var2.toLowerCase().contains("%" + var8.toLowerCase() + "%")) {
                    continue;
                }

                String var18 = var4.invoke((VariableReplacer)this.replacers.get(var4.getReplacer()), var1);
                if (!var18.equals("UNSUPPORTED")) {
                    if (var10[0] <= -1) {
                        var10[0] = 0;
                    }

                    if (var10[1] > var18.length() || var10[1] <= -1) {
                        var10[1] = var18.length();
                    }

                    if (var10[0] != 0 || var10[1] != var18.length()) {
                        var18 = var18.substring(var10[0], var10[1]);
                    }
                }

                var2 = var2.replaceAll(var11 ? var9.pattern().pattern() : "(?i)\\%" + var8 + "\\%", var18);
            }
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(var1.getPlayer(), var2);
        } else {
            return var2;
        }
    }
}
