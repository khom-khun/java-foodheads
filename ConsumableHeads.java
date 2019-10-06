package me.ppsychrite.ConsumableHeads;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;


import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.UUID;
import org.bukkit.OfflinePlayer;

import java.util.logging.Level;

public class ConsumableHeads extends JavaPlugin {

    public class Food {
        String name = null;
        ItemStack item = null;
        ShapedRecipe recipe = null;
    }

    private FileConfiguration config = null;
    public List<Food> food = new ArrayList<Food>();
    public Map<Location, String> placed = new HashMap();


    public int compItem(String name) {

        for(int i = 0; i < food.size(); ++i) {
            SkullMeta comp = (SkullMeta)food.get(i).item.getItemMeta();


            String compName = comp.getDisplayName();
            if(compName.equals(name)) return i;
        }

        return -1;
    }

    public Food makeHead(String foodName) {
        Food food = new Food();

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta)head.getItemMeta();




        meta.setDisplayName(ChatColor.RESET + config.getString("foods." + foodName + ".name"));
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(config.getString("foods." + foodName + ".username")));

        ArrayList<String> desc = new ArrayList<String>();
        desc.add(ChatColor.GOLD + "Yum!");
        desc.add(ChatColor.RESET + "Restores " + ((double)config.getInt("foods." + foodName + ".hunger"))/2 + " bar(s) of hunger");
        desc.add(ChatColor.RESET + "Saturation: " + ((double)config.getInt("foods." + foodName + ".saturation")));

        meta.setLore(desc);
        head.setItemMeta(meta);

        food.item = head;
        food.name = foodName;

        NamespacedKey key = new NamespacedKey(this, "ppsychrite_foodtypes_"+foodName);
        ShapedRecipe recipe = new ShapedRecipe(key, food.item);

        List<String> craftConfig = config.getStringList("foods." + foodName + ".crafting");
        String add = "";


        recipe.shape(craftConfig.get(0), craftConfig.get(1), craftConfig.get(2));
        for(int i = 0; i < craftConfig.size(); ++i) {

            add += craftConfig.get(i).trim();
        }



        for(int i = 0; i < add.length(); ++i){
            Character code = add.charAt(i);

            String choice = config.getString("foods." + foodName + ".codes." + code.toString());


            Material mat = Material.matchMaterial(choice);
            recipe.setIngredient(code, mat);
        }

        Bukkit.addRecipe(recipe);

        food.recipe = recipe;

        return food;
    }


    @Override
    public void onEnable() {

        saveDefaultConfig();
        config = getConfig();

        for(String v : config.getConfigurationSection("foods").getKeys(false)) {
            food.add(makeHead(v));
        }

        if(config.getConfigurationSection("placed") != null) {

            Set<String> foodNames = config.getConfigurationSection("placed").getKeys(false);

            for (String v : foodNames) {


                List<String> data = config.getStringList("placed." + v);

                for (String loc : data) {
                    placed.put(getLocationFromString(loc), v);
                }
            }
        }
        getServer().getPluginManager().registerEvents(new ClickListener(this), this);
    }

    @Override
    public void onDisable() {

        Map<String, List<String>> locationSaves = new HashMap();

        for(Map.Entry<Location, String> entry : placed.entrySet()){
            if(!locationSaves.containsKey(entry.getValue()))
                locationSaves.put(entry.getValue(), new ArrayList<String>());
            locationSaves.get(entry.getValue()).add(getStringFromLocation(entry.getKey()));
        }

        config.set("placed", locationSaves);
        saveConfig();

    }








    public static String getStringFromLocation(Location loc) {
        if (loc == null) {
            return "";
        }
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() ;
    }

    public static Location getLocationFromString(String s) {
        if (s == null || s.trim() == "") {
            return null;
        }
        final String[] parts = s.split(":");
        if (parts.length == 4) {
            World w = Bukkit.getServer().getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            return new Location(w, x, y, z);
        }
        return null;
    }



}
