package me.ppsychrite.ConsumableHeads;



import java.util.Iterator;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockExplodeEvent;

import org.bukkit.event.entity.EntityExplodeEvent;


import java.awt.*;
import java.util.logging.Level;




public class ClickListener implements Listener {

    private ConsumableHeads server;

    public ClickListener(ConsumableHeads server) {
        this.server = server;

    }
    @EventHandler
    public void onPlayerBreaks(BlockBreakEvent e) {
        if(e.isCancelled()) return;

        Block block = e.getBlock();

        if(server.placed.containsKey(block.getLocation()) && (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD)) {
            String name = server.placed.get(block.getLocation());

            int get = server.compItem(name);

            e.setDropItems(false);
            e.getPlayer().getWorld().dropItem(e.getBlock().getLocation(), server.food.get(get).item);

            server.placed.remove(block.getLocation());
            block.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onPlayerPlaces(BlockPlaceEvent e) {
        if(e.isCancelled()) return;


        Block block = e.getBlockPlaced();
        ItemStack item = e.getItemInHand();





        List<String> lore = item.getItemMeta().getLore();
        if(lore == null) return;
        if(item.getType() != Material.PLAYER_HEAD && item.getType() != Material.PLAYER_WALL_HEAD) return;
        server.placed.put(block.getLocation(), item.getItemMeta().getDisplayName());
    }

    @EventHandler
    public void onPlayerClicks(PlayerInteractEvent e) {

        ItemStack food = e.getPlayer().getInventory().getItemInMainHand();
        boolean cont = false;
        if(
                e.getPlayer().getFoodLevel() < 20 &&
                e.getAction() == Action.RIGHT_CLICK_AIR &&
                food.getType() == Material.PLAYER_HEAD &&
                        food.getItemMeta().getLore() != null &&
                food.getItemMeta().getLore().get(0).contains("Yum!") &&
                        food.getItemMeta().getLore().get(1).contains("Restores")
        ) {
            if(food.getAmount() > 1) food.setAmount(food.getAmount()-1);
            else e.getPlayer().getInventory().remove(food);

            String hunger = food.getItemMeta().getLore().get(1);
            String saturation = food.getItemMeta().getLore().get(2);
            String[] data = hunger.split(" ");

            double dbl = Double.parseDouble(data[1]);

            data = saturation.split(" ");

            double dbl2 = Double.parseDouble(data[1]);


            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_BURP, 3.0f, 0.0f);
            e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel() + (int)(dbl * 2));
            e.getPlayer().setSaturation((float)dbl2);
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {
        for(Block block : e.getBlocks()) {
            if (block.getType() == Material.PLAYER_WALL_HEAD ||
                block.getType() == Material.PLAYER_HEAD) {
                e.setCancelled(true);
                break;
            }
        }
    }
    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e) {
        for(Block block : e.getBlocks()) {
            if (block.getType() == Material.PLAYER_WALL_HEAD ||
                    block.getType() == Material.PLAYER_HEAD) {
                e.setCancelled(true);
                break;
            }
        }
    }
    @EventHandler
    public void onBlockExplode(EntityExplodeEvent e) {
        if(e.isCancelled()) return;

        for(Block block : e.blockList()) {
            if(server.placed.containsKey(block.getLocation())) {
                e.setYield(0);
            }

        }

    }




}



