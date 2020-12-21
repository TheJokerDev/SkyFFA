package me.thejokerdev.other;

import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleItem {
    private XMaterial material;
    private int amount = 1;
    private List<String> lore;
    private String displayName;
    private String skinTexture;
    private short data = 0;
    private HashMap<Enchantment, Integer> enchantments;
    private List<ItemFlag> flags;
    private final HashMap<String, String> placeholders;
    private Color color;
    private ItemStack item = null;

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public FireworkEffectMeta getFireworkEffectMeta() {
        return fireworkEffectMeta;
    }
    public SimpleItem setColor(Color var1) {
        if (material.name().contains("LEATHER_")) {
            this.color = var1;
        }

        return this;
    }

    public void setFireworkEffectMeta(FireworkEffectMeta fireworkEffectMeta) {
        this.fireworkEffectMeta = fireworkEffectMeta;
    }

    private FireworkEffectMeta fireworkEffectMeta = null;

    public SimpleItem(ItemStack itemStack) {
        this.lore = new ArrayList();
        this.enchantments = new HashMap();
        this.flags = new ArrayList();
        this.material = XMaterial.matchXMaterial(itemStack);
        this.amount = itemStack.getAmount();
        this.data = itemStack.getDurability();
        if (itemStack.getItemMeta() != null && itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta)meta;
                skinTexture = skullMeta.getOwner();
            }
            this.lore = meta.getLore();
            this.displayName = meta.getDisplayName();
            this.flags = new ArrayList(meta.getItemFlags());
        }

        this.enchantments = new HashMap(itemStack.getEnchantments());
        this.placeholders = new HashMap();
    }

    public SimpleItem(Boolean skull, String skinTexture) {
        this.material = XMaterial.PLAYER_HEAD;
        this.data = 3;
        this.skinTexture = skinTexture;
        this.enchantments = new HashMap();
        this.flags = new ArrayList();
        this.lore = new ArrayList();
        this.placeholders = new HashMap();
    }

    public SimpleItem(XMaterial material) {
        this.material = material;
        this.enchantments = new HashMap();
        this.flags = new ArrayList();
        this.lore = new ArrayList();
        this.placeholders = new HashMap();
    }

    public SimpleItem(String displayName) {
        this.material = XMaterial.STONE;
        this.displayName = displayName;
        this.enchantments = new HashMap();
        this.flags = new ArrayList();
        this.lore = new ArrayList();
        this.placeholders = new HashMap();
    }

    public SimpleItem addPlaceholder(String key, String value) {
        this.placeholders.put(key, value);
        return this;
    }

    public SimpleItem removePlaceholder(String key) {
        this.placeholders.remove(key);
        return this;
    }

    public SimpleItem setMaterial(XMaterial material) {
        this.material = material;
        return this;
    }

    public SimpleItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public SimpleItem setLore(List<String> lore) {
        this.lore = new ArrayList(lore);
        return this;
    }

    public SimpleItem setLore(String... lore) {
        this.lore = Utils.toList(lore);
        return this;
    }

    public SimpleItem setFirstLoreLine(String text) {
        List<String> lore = new ArrayList();
        lore.add(text);
        lore.addAll(this.lore);
        this.lore = lore;
        return this;
    }

    public SimpleItem addLoreLine(String text) {
        this.lore.add(text);
        return this;
    }

    public SimpleItem setDurability(int i){
        this.data = (short)i;
        return this;
    }

    public SimpleItem addLoreLines(String... lore) {
        this.lore.addAll(Utils.toList(lore));
        return this;
    }

    public SimpleItem setLoreLine(int index, String text) {
        if (index < 0) {
            index = this.lore.size();
        }

        this.lore.set(index, text);
        return this;
    }

    public SimpleItem removeLastLoreLine() {
        this.lore.remove(this.lore.size() - 1);
        return this;
    }

    public SimpleItem removeLoreLine(int index) {
        if (index >= this.lore.size()) {
            index = this.lore.size() - 1;
        }

        if (index < 0) {
            index = 0;
        }

        this.lore.remove(index);
        return this;
    }

    public SimpleItem setSkin(String skin) {
        this.skinTexture = skin;
        return this;
    }

    public SimpleItem addEnchantment(Enchantment enchantment) {
        return this.addEnchantment(enchantment, 1);
    }

    public SimpleItem addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public SimpleItem removeEnchantment(Enchantment enchantment) {
        this.enchantments.remove(enchantment);
        return this;
    }

    public SimpleItem addFlag(ItemFlag flag) {
        this.flags.add(flag);
        return this;
    }

    public SimpleItem addFlags(ItemFlag... flags) {
        this.flags.addAll(Utils.toList(flags));
        return this;
    }

    public SimpleItem removeFlag(ItemFlag flag) {
        this.flags.remove(flag);
        return this;
    }

    public SimpleItem setShowEnchantments(boolean showEnchantments) {
        ItemFlag flag = ItemFlag.HIDE_ENCHANTS;
        if (showEnchantments) {
            this.flags.remove(flag);
        } else {
            this.flags.add(flag);
        }

        return this;
    }

    public SimpleItem setGlowing(boolean glowing) {
        ItemFlag flag = ItemFlag.HIDE_ENCHANTS;
        if (glowing) {
            this.addFlag(flag);
            this.addEnchantment(Enchantment.DURABILITY);
        } else {
            this.removeFlag(flag);
            this.removeEnchantment(Enchantment.DURABILITY);
        }

        return this;
    }

    public SimpleItem setShowAttributes(boolean showAttributes) {
        ItemFlag flag = ItemFlag.HIDE_ATTRIBUTES;
        if (showAttributes) {
            this.flags.remove(flag);
        } else {
            this.flags.add(flag);
        }

        return this;
    }

    public SimpleItem setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    public SimpleItem setEnchantments(HashMap<Enchantment, Integer> enchantments) {
        this.enchantments = new HashMap(enchantments);
        return this;
    }

    public SimpleItem setFlags(List<ItemFlag> flags) {
        this.flags = new ArrayList(flags);
        return this;
    }

    public XMaterial getMaterial() {
        return this.material;
    }

    public int getAmount() {
        return this.amount;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public short getDurability(){
        return this.data;
    }

    public String getSkinTexture() {
        return this.skinTexture;
    }

    public HashMap<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    public List<ItemFlag> getFlags() {
        return this.flags;
    }

    public boolean hasEnchantments() {
        return this.getEnchantments().size() != 0;
    }

    public boolean hasFlags() {
        return this.getFlags().size() != 0;
    }

    public boolean hasSkin() {
        return this.getSkinTexture() != null;
    }

    public SimpleItem duplicate() {
        return (new SimpleItem(this.getMaterial())).setAmount(this.getAmount()).setLore(this.getLore()).setDisplayName(this.getDisplayName()).setSkin(this.getSkinTexture()).setEnchantments(this.getEnchantments()).setFlags(this.getFlags());
    }

    public ItemStack build() {
        if (this.getDisplayName() == null) {
            if (this.getMaterial() != null) {
                this.setDisplayName(this.displayName);
            } else {
                this.setDisplayName("&cDisplayName is null!. You can also set just '& 7'");
            }
        }
        if (item == null){
            item = material.parseItem();
        }
        Object meta = item.getItemMeta();
        if (meta instanceof LeatherArmorMeta && this.color != null) {
            LeatherArmorMeta LMeta = (LeatherArmorMeta)meta;
            LMeta.setColor(this.color);
            item.setItemMeta(LMeta);
        }
        if (meta instanceof SkullMeta && this.skinTexture != null) {
            SkullMeta SMeta = (SkullMeta) meta;
            SMeta.setOwner(this.skinTexture);
            item.setItemMeta(SMeta);
        }
        if (meta instanceof FireworkEffectMeta && fireworkEffectMeta != null) {
            item.setItemMeta(fireworkEffectMeta);
        }
        meta = item.getItemMeta();
        if (meta != null) {
            ((ItemMeta)meta).setDisplayName(this.apply(Utils.ct(this.getDisplayName())));
            ((ItemMeta)meta).setLore(this.apply(Utils.ct(this.getLore())));
            Object finalMeta = meta;
            this.getFlags().forEach((xva$0) -> ((ItemMeta) finalMeta).addItemFlags(xva$0));
            item.setDurability(this.data);
        }
        ((ItemMeta)meta).addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS});
        item.setItemMeta(((ItemMeta)meta));
        item.addUnsafeEnchantments(this.getEnchantments());
        item.setAmount(this.getAmount());
        return item;
    }

    private String apply(String text) {
        AtomicReference<String> r = new AtomicReference(text);
        this.placeholders.forEach((k, v) -> {
            r.set(r.get().replace(k, v));
        });
        return r.get();
    }

    private List<String> apply(List<String> list) {
        List<String> r = new ArrayList();
        list.forEach((s) -> {
            r.add(this.apply(s));
        });
        return r;
    }

    public boolean hasLore() {
        return this.getLore() != null;
    }

    public boolean hasDisplayName() {
        return this.getDisplayName() != null;
    }

    public boolean hasDurability(){
        return this.getDurability() != 0;
    }
}
