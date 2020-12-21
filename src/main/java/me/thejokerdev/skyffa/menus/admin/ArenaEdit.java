package me.thejokerdev.skyffa.menus.admin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.thejokerdev.other.Dialog;
import me.thejokerdev.other.SimpleItem;
import me.thejokerdev.other.XMaterial;
import me.thejokerdev.skyffa.Main;
import me.thejokerdev.skyffa.arena.Arena;
import me.thejokerdev.skyffa.arena.ArenaManager;
import me.thejokerdev.skyffa.arena.ArenaState;
import me.thejokerdev.skyffa.menus.Menu;
import me.thejokerdev.skyffa.menus.MenuListener;
import me.thejokerdev.skyffa.utils.ArraysUtil;
import me.thejokerdev.skyffa.utils.ItemsUtil;
import me.thejokerdev.skyffa.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaEdit extends Menu {

    public ArenaEdit(Player p, Arena var1){
        super(p, "arenaEditor", "Editor de Arenas | "+Utils.getArenaDName(var1), 3);
    }
    @Override
    public void onOpen(InventoryOpenEvent var1) {
        update();
    }

    @Override
    public void onClose(InventoryCloseEvent var1) {
        MenuListener.getPlayerMenus(getPlayer()).remove("arenaEditor");
    }

    @Override
    public void onClick(InventoryClickEvent var1) {
        ItemStack click = var1.getCurrentItem();
        Arena arena = ArraysUtil.arenaEdit.get(getPlayer());
        if (arena == null){
            getPlayer().closeInventory();
            return;
        }
        if (click.isSimilar(getEnableItem(arena).build())){
            String str = arena.getName();
            arena.restart();
            arena = new Arena(str, true);
            ArraysUtil.arenaEdit.put(getPlayer(), arena);
            update();
            return;
        }
        if (click.isSimilar(getDisableItem(arena).build())){
            String str = arena.getName();
            arena.restart();
            arena = new Arena(str, false);
            ArraysUtil.arenaEdit.put(getPlayer(), arena);
            update();
            return;
        }
        if (arena.getArenaState() != ArenaState.LOADING){
            Utils.sendMessage(getPlayer(), true, "&c¡Primero debes desactivar la arena!");
            return;
        }
        if (click.isSimilar(getDisplayNameItem(arena).build())){
            getPlayer().closeInventory();
            ArraysUtil.inDialog.add(getPlayer());
            Arena finalArena = arena;
            new Dialog(Main.getPlugin(), getPlayer()) {
                @Override
                public String getTitle() {
                    return "&aNombre de pila";
                }

                @Override
                public String getSubtitle() {
                    return "&7Cambia o establece la opción";
                }

                @Override
                public String getActionbar() {
                    return "&7Actual: &r"+ finalArena.getDisplayName();
                }

                @Override
                public boolean onResult(String var1) {
                    if (var1 != null){
                        finalArena.getConfig().set("general.displayName", var1);
                        finalArena.setDisplayName(var1);
                        Utils.sendMessage(getPlayer(), true, "&7Ahora, has establecido el nombre de pila de "+ finalArena.getName()+ " a: &r"+ finalArena.getDisplayName());
                    }
                    return true;
                }

                @Override
                public void onDialogClose() {
                    ArraysUtil.inDialog.remove(getPlayer());
                    new ArenaEdit(getPlayer(), ArraysUtil.arenaEdit.get(getPlayer()));
                    getPlayer().openInventory(MenuListener.getPlayerMenu(getPlayer(), "arenaEditor").getInventory());
                }
            }.setRecall(player -> {});
            return;
        }
        if (click.isSimilar(getMinPlayersItem(arena).build())){
            if (var1.isLeftClick()){
                arena.setMinPlayers(arena.getMinPlayers()+1);
            }
            if (var1.isRightClick()){
                if (arena.getMinPlayers() > 0) {
                    arena.setMinPlayers(arena.getMinPlayers() - 1);
                } else {
                    Utils.sendMessage(getPlayer(), true, "&c¡No puedes poner jugadores negativos!");
                    return;
                }
            }
        }
        if (click.isSimilar(getMaxPlayersItem(arena).build())){
            if (var1.isLeftClick()){
                arena.setMaxPlayers(arena.getMaxPlayers()+1);
            }
            if (var1.isRightClick()){
                if (arena.getMaxPlayers() > arena.getMinPlayers()) {
                    arena.setMaxPlayers(arena.getMaxPlayers() - 1);
                } else {
                    Utils.sendMessage(getPlayer(), true, "&c¡Debes poner una cantidad más alta que los jugadores mínimos!");
                    return;
                }
            }
        }
        if (click.isSimilar(getSpawnsItem(arena).build())){
            getPlayer().openInventory(MenuListener.getPlayerMenu(getPlayer(), "arenaEditorSpawns").getInventory());
            return;
        }
        if (click.isSimilar(getWandItem().build())){
            getPlayer().closeInventory();
            getPlayer().chat("//wand");
            getPlayer().updateInventory();
            return;
        }
        if (click.isSimilar(getSpawnItem().build())){
            getPlayer().closeInventory();
            getPlayer().getInventory().addItem(ItemsUtil.getSpawnItem());
            getPlayer().updateInventory();
            return;
        }
        if (click.isSimilar(getSaveSchem(arena).build())){
            getPlayer().closeInventory();
            WorldEditPlugin worldEditPlugin;
            worldEditPlugin = (WorldEditPlugin) Main.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit");
            Selection sel = worldEditPlugin.getSelection(getPlayer());

            if (sel instanceof Polygonal2DSelection) {
                Polygonal2DSelection polySel = (Polygonal2DSelection) sel;
                Vector min = polySel.getNativeMinimumPoint();
                Vector max = polySel.getNativeMaximumPoint();
                if (min != null && max != null){
                    arena.saveSchematic(min, max);
                    getPlayer().closeInventory();
                    return;
                }
            }
            else if (sel instanceof CuboidSelection) {
                Vector min = sel.getNativeMinimumPoint();
                Vector max = sel.getNativeMaximumPoint();
                if (min != null && max != null){
                    arena.saveSchematic(min, max);
                    getPlayer().closeInventory();
                    return;
                }
            }
            return;
        }
        update();
    }

    @Override
    public void update() {
        Arena arena = ArraysUtil.arenaEdit.get(getPlayer());
        setItem(0, getDisplayNameItem(arena));
        setItem(1, getMinPlayersItem(arena));
        setItem(2, getMaxPlayersItem(arena));
        setItem(3, getSpawnsItem(arena));

        if (arena.getArenaState() == ArenaState.LOADING){
            setItem(22, getEnableItem(arena));
        } else {
            setItem(22, getDisableItem(arena));
        }
        WorldEditPlugin worldEditPlugin;
        worldEditPlugin = (WorldEditPlugin) Main.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit");
        Selection sel = worldEditPlugin.getSelection(getPlayer());
        if (sel != null) {
            if (sel.getMaximumPoint() != null && sel.getMinimumPoint() != null) {
                setItem(17, getSaveSchem(arena));
            }
        }
        setItem(25, getWandItem());
        setItem(26, getSpawnItem());
    }

    private SimpleItem getDisplayNameItem(Arena arena){
        SimpleItem item = new SimpleItem(XMaterial.PAPER);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&aNombre de pila");
        item.setLore("&7Cambia o establece el nombre", "&7de pila que tendrá la arena.", "", "&7Actual: &r"+arena.getDisplayName()
        , "", "&eClick para interactuar");
        return item;
    }
    private SimpleItem getSaveSchem(Arena arena){
        SimpleItem item = new SimpleItem(XMaterial.EMERALD);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&aGuardar esquemática");
        item.setLore("&7Clic para guardar esquemática");
        return item;
    }
    private SimpleItem getEnableItem(Arena arena){
        SimpleItem item = new SimpleItem(XMaterial.WOODEN_SWORD);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&aActivar Arena");
        item.setLore("&7Cambia el modo de la arena", "&7para poder jugarla.", "", "&7Actual: &7Desactivada"
                , "", "&eClick para interactuar");
        return item;
    }
    private SimpleItem getDisableItem(Arena arena){
        SimpleItem item = new SimpleItem(XMaterial.DIAMOND_SWORD);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&cDesactivar Arena");
        item.setLore("&7Cambia el modo de la arena", "&7para poder editarla.", "", "&7Actual: &aActivada"
                , "", "&eClick para interactuar");
        return item;
    }
    private SimpleItem getMinPlayersItem(Arena arena){
        SimpleItem item = new SimpleItem(XMaterial.LIME_TERRACOTTA);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&aMínimo de jugadores");
        item.setLore("&7Cambia o establece el número", "&7mínimo de jugadores.", "", "&7Actual: &r"+arena.getMinPlayers()
                , "", "&eClick izquierdo para aumentar", "&eClick derecho para disminuir");
        return item;
    }
    private SimpleItem getMaxPlayersItem(Arena arena){
        SimpleItem item = new SimpleItem(XMaterial.RED_TERRACOTTA);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&aMáximo de jugadores");
        item.setLore("&7Cambia o establece el número", "&7máximo de jugadores.", "", "&7Actual: &r"+arena.getMaxPlayers()
                , "", "&eClick izquierdo para aumentar", "&eClick derecho para disminuir");
        return item;
    }
    private SimpleItem getSpawnItem(){
        SimpleItem item = new SimpleItem(XMaterial.NETHER_STAR);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&aHerramienta de Spawns");
        item.setLore("&7Establece el punto de", "&7aparición de jugadores."
                , "", "&eClick en bloque para añadir");
        return item;
    }
    private SimpleItem getWandItem(){
        SimpleItem item = new SimpleItem(XMaterial.WOODEN_AXE);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&aHerramienta de esquemáticas");
        item.setLore("&7Crea la esquemática para", "&7guardarla en la carpeta."
                , "", "&eSelecciona el punto más alto/bajo", "&epara guardarlo como esquemática");
        return item;
    }
    private SimpleItem getSpawnsItem(Arena arena){
        SimpleItem item = new SimpleItem(XMaterial.ENDER_PEARL);
        item.setDurability(item.getMaterial().getData());
        item.setDisplayName("&aPuntos de aparición");
        item.setLore("&7Modifica los puntos de", "&7aparición de la arena.", ""
                , "&7N° de Spawns: &e"+arena.getSpawnPoints().size()
                , "", "&eClic para verlos");
        return item;
    }
}
