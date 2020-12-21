package me.thejokerdev.skyffa.utils;

import me.thejokerdev.skyffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.HashMap;
import java.util.Iterator;

public class BoardAPI {
    public BoardAPI() {
    }

    public static void scoredSidebar(Player var0, String var1, HashMap<String, Integer> var2) {
        if (var1 == null) {
            var1 = "Unamed board";
        }

        if (var1.length() > 32) {
            var1 = var1.substring(0, 32);
        }

        String var4;
        label39:
        for(; var2.size() > 16; var2.remove(var4)) {
            var4 = (String)var2.keySet().toArray()[0];
            int var5 = (Integer)var2.get(var4);
            Iterator var6 = var2.keySet().iterator();

            while(true) {
                String var7;
                do {
                    if (!var6.hasNext()) {
                        continue label39;
                    }

                    var7 = (String)var6.next();
                } while((Integer)var2.get(var7) >= var5 && ((Integer)var2.get(var7) != var5 || var7.compareTo(var4) >= 0));

                var4 = var7;
                var5 = (Integer)var2.get(var7);
            }
        }

        String finalVar = var1;
        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
            if (var0 != null && var0.isOnline()) {
                if (Bukkit.getScoreboardManager().getMainScoreboard() != null && Bukkit.getScoreboardManager().getMainScoreboard() == var0.getScoreboard()) {
                    var0.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }

                if (var0.getScoreboard() == null) {
                    var0.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }

                Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
                    Objective var3 = var0.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
                    if (var3 == null) {
                        var3 = var0.getScoreboard().registerNewObjective(finalVar.length() > 16 ? finalVar.substring(0, 15) : finalVar, "dummy");
                    }

                    var3.setDisplayName(finalVar);
                    if (var3.getDisplaySlot() == null || var3.getDisplaySlot() != DisplaySlot.SIDEBAR) {
                        var3.setDisplaySlot(DisplaySlot.SIDEBAR);
                    }

                    Iterator var14 = var2.keySet().iterator();

                    while(true) {
                        String var5;
                        do {
                            if (!var14.hasNext()) {
                                var14 = var0.getScoreboard().getEntries().iterator();

                                while(var14.hasNext()) {
                                    var5 = (String)var14.next();
                                    if (var3.getScore(var5).isScoreSet() && !var2.containsKey(var5)) {
                                        var0.getScoreboard().resetScores(var5);
                                    }
                                }

                                return;
                            }

                            var5 = (String)var14.next();
                        } while(var3.getScore(var5).isScoreSet() && var3.getScore(var5).getScore() == (Integer)var2.get(var5));

                        var3.getScore(var5).setScore(var2.get(var5));
                    }
                });
            }
        });
    }
}
