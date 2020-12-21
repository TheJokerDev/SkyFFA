package me.thejokerdev.skyffa.player;

import org.bukkit.Location;

import java.util.HashMap;

public class SkyData {
    private HashMap<String, Object> data = new HashMap();

    public SkyData() {
    }

    public void addData(String var1, Object var2) {
        this.data.put(var1, var2);
    }

    public void removeData(String var1) {
        this.data.remove(var1);
    }

    public boolean hasData(String var1) {
        return this.data.containsKey(var1);
    }

    public Object get(String var1) {
        return this.data.get(var1);
    }

    public String getString(String var1) {
        return (String)this.data.get(var1);
    }

    public int getInt(String var1) {
        return (Integer)this.data.get(var1);
    }

    public Location getLocation(String var1) {
        return (Location)this.data.get(var1);
    }

    public boolean getBoolean(String var1) {
        return (Boolean)this.data.get(var1);
    }

    public float getFloat(String var1) {
        return (Float)this.data.get(var1);
    }

    public double getDouble(String var1) {
        return !this.data.containsKey(var1) ? 0.0D : (Double)this.data.get(var1);
    }

    public long getLong(String var1) {
        return !this.data.containsKey(var1) ? 0L : (Long)this.data.get(var1);
    }

    public void clearData() {
        this.data.clear();
    }
}
