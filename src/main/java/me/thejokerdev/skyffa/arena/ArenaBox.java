package me.thejokerdev.skyffa.arena;

import me.thejokerdev.skyffa.box.Box;
import me.thejokerdev.skyffa.box.BoxManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

public class ArenaBox {
    private final Location base;
    private final World world;
    private int x;
    private int y;
    private int z;

    public ArenaBox(Location paramLocation) {
        this.base = paramLocation;
        this.x = this.base.getBlockX();
        this.y = this.base.getBlockY();
        this.z = this.base.getBlockZ();
        this.world = paramLocation.getWorld();
    }


    public void setBox(String boxName) {
        Box[] boxes = BoxManager.getBoxes();
        Box box = null;
        for (Box testBox : boxes){
            if (testBox.getName().equalsIgnoreCase(boxName)){
                box=testBox;
            }
        }
        if (box == null){
            Bukkit.getConsoleSender().sendMessage("Box "+boxName+" not found on boxes.yml.");
            //log message = "Box "+boxName+" not found on boxes.yml."
            return;
        }
        List<String> boxSetting = box.getBoxSetting();


        for (String s : boxSetting) {
            String[] var1 = s.split(",");
            if (var1.length < 3) {
                //SkyWars.logError("Location can't be obtained from (world,x,y,z needed)'" + var0 + "'");
            }
            if (var1.length != 5) {
                Bukkit.getConsoleSender().sendMessage("Error not much arguments");
            } else {
                int addX = Integer.parseInt(var1[0]);
                int addY = Integer.parseInt(var1[1]);
                int addZ = Integer.parseInt(var1[2]);
                int material = Integer.parseInt(var1[3]);
                int data = Integer.parseInt(var1[4]);
                    this.world.getBlockAt(x + addX, y + addY-1, z + addZ).setTypeIdAndData(material, (byte) data, true);
            }
        }

    }


    public Location getLocation() {
        return this.base;
    }


    public void removeBase() {
        this.world.getBlockAt(x, y-1, z).setType(Material.AIR);
    }


    public void removeAll(String boxName) {
        Box[] boxes = BoxManager.getBoxes();
        Box box = null;
        for (Box testBox : boxes){
            if (testBox.getName().equalsIgnoreCase(boxName)){
                box=testBox;
            }
        }
        if (box == null){
            Bukkit.getConsoleSender().sendMessage("Box "+boxName+" not found on boxes.yml.");
            //log message = "Box "+boxName+" not found on boxes.yml."
            return;
        }
        List<String> boxSetting = box.getBoxSetting();


        for (String s : boxSetting) {
            String[] var1 = s.split(",");
            if (var1.length < 3) {
                //SkyWars.logError("Location can't be obtained from (world,x,y,z needed)'" + var0 + "'");
            }
            if (var1.length != 5) {
                Bukkit.getConsoleSender().sendMessage("Error not much arguments");
            } else {
                int addX = Integer.parseInt(var1[0]);
                int addY = Integer.parseInt(var1[1]);
                int addZ = Integer.parseInt(var1[2]);
                this.world.getBlockAt(x + addX, y + addY-1, z + addZ).setType(Material.AIR);
            }
        }
    }
}
