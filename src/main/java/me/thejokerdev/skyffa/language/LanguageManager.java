package me.thejokerdev.skyffa.language;

import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.command.CommandSender;

import java.io.File;

public class LanguageManager {

    public static void loadTranslations(Main plugin){
        File folder = Main.getTranslationsFolder;
        if (!folder.exists()){
            folder.mkdir();
        }
        if (!new File(Main.getTranslationsFolder, "en_US.yml").exists()){
            plugin.saveResource("translations/en_US.yml", false);
        }
        if (!new File(Main.getTranslationsFolder, "es_ES.yml").exists()){
            plugin.saveResource("translations/es_ES.yml", false);
        }
        File[] files = folder.listFiles();
        if(files == null) return;
        if(files.length == 0) return;
        int i = 0;
        for(File file : files){
            if(!file.getName().endsWith(".yml") && !file.getName().contains("_")) continue;
            i++;
        }
        CommandSender p = null;
        Utils.sendMessage(p, true, "&a¡&e"+i + " &atraducciones encontradas y cargadas!");
    }

}
