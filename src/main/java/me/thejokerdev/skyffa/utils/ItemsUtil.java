package me.thejokerdev.skyffa.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.other.SimpleItem;
import me.thejokerdev.other.SkullUtils;
import me.thejokerdev.other.XMaterial;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class ItemsUtil {



    public static ItemStack getSpawnItem(){
        SimpleItem item = new SimpleItem(XMaterial.GOLDEN_AXE);

        item.setDisplayName("&aHerramienta de spawns");

        item.setLore("&eClick para añadir");

        return item.build();
    }
    public static ItemStack getCornersItem(){
        SimpleItem item = new SimpleItem(XMaterial.STICK);

        item.setDisplayName("&aHerramienta de límites");

        item.setLore("&eClick izquierdo para 1r punto", "&eClick derecho para 2do punto");

        return item.build();
    }

    public static ItemStack getLeaveItem(){
        SimpleItem item = new SimpleItem(XMaterial.RED_BED);

        item.setDisplayName("&cSalir");

        item.setLore("&eClick para salir");

        return item.build();
    }


    public static boolean compareItems(ItemStack item1, ItemStack item2) {
        boolean bool = false;
        if (item1 != null &&item2 != null && item1.getType() != XMaterial.AIR.parseMaterial()) {
            if (item1.getType() == item2.getType() && item1.getAmount() == item2.getAmount()) {
                if (item1.hasItemMeta() && item1.getItemMeta().hasDisplayName()) {
                    if (item1.getItemMeta().getDisplayName().equalsIgnoreCase(item2.getItemMeta().getDisplayName())) {
                        bool = true;
                    }
                }
            }
        }
        return bool;
    }
    public static ItemStack createShortItem(String str){
        SimpleItem item = new SimpleItem(XMaterial.BEDROCK).setDisplayName("&7&oName null");
        if (str.contains(",")) {
            String[] itemConfig = str.split(",");
            String materialStr = itemConfig[0];
            int data = 0;
            if (materialStr.contains(":")) {
                data = Integer.valueOf(materialStr.split(":")[1]);
                materialStr = materialStr.split(":")[0];
            }
            XMaterial material = XMaterial.valueOf(materialStr.toUpperCase());
            if (material != null) {
                item.setMaterial(material);
                item.setDurability(data);
            }
            String name = itemConfig[1];
            if (name.startsWith("name:")) {
                name = name.replace("name:", "");
                item.setDisplayName(name);
            }
        } else {
            item.setMaterial(XMaterial.valueOf(str.toUpperCase()));
            item.setDurability(item.getMaterial().getData());
        }
        return item.build();
    }

    public static SimpleItem createItem(ConfigurationSection section, Player p) {
        String[] var1;
        int int1 = 0;
        int int2 = 0;
        int int3 = 0;
        SimpleItem item = new SimpleItem(XMaterial.BEDROCK).setDisplayName("&7&oName null");
        boolean hasMaterial = section.get("material") != null;
        XMaterial material = null;
        boolean hasData = section.get("data") != null;
        boolean hasRemoveAttributes = section.get("removeAttributes") != null;
        int data;
        boolean hasAmount = section.get("amount") != null;
        int amount;
        boolean hasGlow = section.get("glowing") != null;
        boolean glowing;
        boolean hasSkullData = section.get("skull") != null;
        String skullData;
        boolean hasColor = section.get("color") != null;
        String color;
        boolean hasPotionData = section.get("potion") != null;
        boolean hasFireWorkData = section.get("firework") != null;
        boolean hasDisplayName = section.get("meta.name") != null;
        boolean hasLore = section.get("meta.lore") != null;

        if (hasMaterial) {
            try {
                material = XMaterial.valueOf(section.getString("material").toUpperCase());
            } catch (IllegalArgumentException e) {
                Utils.sendMessage(Bukkit.getConsoleSender(), true, "&cMaterial on " + section.getName() + " is not valid.");
            }
            if (material != null) {
                item.setMaterial(material);
            }
        }
        if (hasData) {
            data = section.getInt("data");
            item.setDurability((short) data);
        }
        if (hasAmount) {
            amount = section.getInt("amount");
            item.setAmount(amount);
        }
        if (hasGlow) {
            glowing = section.getBoolean("glowing");
            item.setGlowing(glowing);
        }
        if (material == XMaterial.PLAYER_HEAD && hasSkullData) {
            skullData = section.getString("skull").replaceAll("%player_name%", p.getName());
            if (skullData.startsWith("base-")) {
                skullData = skullData.replace("base-", "");
                item.setItem(SkullUtils.getHead(skullData));
            } else if (skullData.startsWith("uuid-")) {
                skullData = skullData.replace("uuid-", "");
                UUID uuid = UUID.fromString(skullData);
                item.setItem(SkullUtils.getHead(uuid));
            } else if (skullData.startsWith("name-")) {
                skullData = skullData.replace("name-", "");
                OfflinePlayer pf = Bukkit.getOfflinePlayer(skullData);
                item.setItem(SkullUtils.getHead(pf));
            } else if (skullData.startsWith("url-")) {
                skullData = skullData.replace("url-", "");
                skullData = "http://textures.minecraft.net/texture/"+skullData;
                item.setItem(SkullUtils.getHead(skullData));
            }
        }
        if (material == XMaterial.FIREWORK_STAR && hasFireWorkData) {
            ItemMeta meta = item.build().getItemMeta();
            FireworkEffectMeta metaFw = (FireworkEffectMeta) meta;
            color = section.getString("firework");
            Color color1 = null;
            var1 = color.split("-");
            if (var1.length == 3) {
                int1 = Utils.isNumeric(var1[0]) ? Integer.parseInt(var1[0]) : 0;
                int2 = Utils.isNumeric(var1[1]) ? Integer.parseInt(var1[1]) : 0;
                int3 = Utils.isNumeric(var1[2]) ? Integer.parseInt(var1[2]) : 0;
            } else {
                int1 = 0;
                int2 = 0;
                int3 = 0;
            }
            color1 = Color.fromRGB(int1, int2, int3);
            FireworkEffect effect = FireworkEffect.builder().withColor(color1).build();
            metaFw.setEffect(effect);
            item.setFireworkEffectMeta(metaFw);
        }
        if ((material.name().contains("LEATHER")) && hasColor) {
            color = section.getString("color");
            Color color1;
            var1 = color.split("-");
            if (var1.length == 3) {
                int1 = Utils.isNumeric(var1[0]) ? Integer.parseInt(var1[0]) : 0;
                int2 = Utils.isNumeric(var1[1]) ? Integer.parseInt(var1[1]) : 0;
                int3 = Utils.isNumeric(var1[2]) ? Integer.parseInt(var1[2]) : 0;
            } else {
                int1 = 0;
                int2 = 0;
                int3 = 0;
            }
            color1 = Color.fromRGB(int1, int2, int3);
            item.setColor(color1);
        }
        if (hasDisplayName) {
            item.setDisplayName(section.getString("meta.name"));
        }
        if (hasLore) {
            item.setLore(section.getStringList("meta.lore"));
        }
        if (hasRemoveAttributes) {
        }
        return setPlaceHolders(item, p);
    }

    public static SimpleItem setPlaceHolders(SimpleItem item, Player p) {
        SimpleItem simpleItem = item;
        simpleItem.setDisplayName(PlaceholderAPI.setPlaceholders(p, simpleItem.getDisplayName()));
        for (int i = 0; i < simpleItem.getLore().size(); i++) {
            simpleItem.getLore().set(i, PlaceholderAPI.setPlaceholders(p, simpleItem.getLore().get(i)));
        }
        return simpleItem;
    }
}
