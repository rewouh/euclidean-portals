package me.rewu.euclidean_portals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Candle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {

    @EventHandler
    public void onCandleLit(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block b = event.getClickedBlock();

        if (b.getType() != Material.CANDLE || event.getMaterial() != Material.FLINT_AND_STEEL) return;

        Candle candle = (Candle) b.getBlockData();

        if (candle.isLit()) return;

        PortalsAPI.checkPortal(b.getLocation());
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();

        if (item.getItemStack().getType() != Material.AMETHYST_SHARD) return;

        Player player = event.getPlayer();

        if (!PortalsAPI.checkPortal(player.getLocation())) return;

        item.remove();
    }
}
