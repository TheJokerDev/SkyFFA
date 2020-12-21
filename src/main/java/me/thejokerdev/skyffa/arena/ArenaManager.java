package me.thejokerdev.skyffa.arena;

import me.thejokerdev.skyffa.Main;

import java.io.File;
import java.util.*;

public class ArenaManager {
    public static HashMap<String, Arena> arenas = new HashMap<>();

    public static HashMap<String, Arena> getArenas() {
        return arenas;
    }

    public static Arena getGameCalculated() {
        HashMap var0 = new HashMap();
        Arena[] var1 = getJoinableArenas().toArray(new Arena[0]);
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            Arena var4 = var1[var3];
            double var5 = (double) var4.getAlivePlayers() / (double) var4.getMaxPlayers();
            if (var5 < 1.0D) {
                var0.put(var4, var5);
            }
        }

        ArrayList var7 = new ArrayList();
        double var8 = 0.0D;
        Iterator var9 = var0.entrySet().iterator();

        while (var9.hasNext()) {
            Map.Entry var11 = (Map.Entry) var9.next();
            if ((Double) var11.getValue() == var8) {
                var7.add(var11.getKey());
            }

            if ((Double) var11.getValue() > var8) {
                var8 = (Double) var11.getValue();
                var7.clear();
                var7.add(var11.getKey());
            }
        }

        if (var7.size() == 0) {
            return null;
        } else if (var7.size() == 1) {
            return (Arena) var7.get(0);
        } else {
            int var10 = (new Random()).nextInt(var7.size());
            return (Arena) var7.get(var10);
        }
    }

    public static Arena getJoinableGame() {
        return getGameCalculated();
    }

    public static void initArenas(){
        arenas.clear();
        File folder = Main.getArenasFolder;
        if (folder.exists() && folder.listFiles().length > 0){
            for (File file1 : folder.listFiles()){
                if (file1.isDirectory()){
                    new Arena(file1.getName(), true);
                }
            }
        }
    }

    public static List<Arena> getJoinableArenas(){
        List<Arena> arenas1 = new ArrayList<>();
        for (Arena a1 : arenas.values()){
            if (a1.getArenaState() == ArenaState.WAITING || a1.getArenaState() == ArenaState.STARTING){
                arenas1.add(a1);
            }
        }
        return arenas1;
    }

    public static Arena getArena(String arena){
        return arenas.get(arena);
    }
}
