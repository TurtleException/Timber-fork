package me.teeage.timber;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Timber extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // ignore if sneaking
        if (event.getPlayer().isSneaking()) return;
        // check permission
        if (!event.getPlayer().hasPermission("timber.use")) return;
        // check if item in use
        if (event.getPlayer().getItemInUse() == null) return;
        if (!this.isAxe(event.getPlayer().getItemInUse().getType())) return;
        // check block material
        if (!this.isLog(event.getBlock().getType())) return;

        this.dropTree(event.getBlock().getLocation(), event.getPlayer());
    }

    private void dropTree(Location location, Player player) {
        List<Block> blocks = new LinkedList<>();

        // should always be true because the location is retrieved from an event
        assert location.getWorld() != null;

        Location buff = location.clone();

        for (int i = location.getBlockY(); i <= location.getWorld().getMaxHeight(); i++) {
            if (isLog(buff.getBlock().getType()))
                blocks.add(buff.getBlock());
            else
                break;

            buff = buff.add(0, 1, 0);
        }

        /*
         * Technically the player could switch the item in the main hand but since this is a sub-tick
         * operation that is not really a plausible issue.
         */

        for (Block block : blocks)
            player.breakBlock(block);
    }

    private boolean isAxe(Material material) {
        return switch (material) {
            case WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE -> true;
            default -> false;
        };
    }

    private boolean isLog(Material material) {
        return switch (material) {
            case OAK_LOG, BIRCH_LOG, SPRUCE_LOG, DARK_OAK_LOG, ACACIA_LOG, JUNGLE_LOG, CRIMSON_STEM, WARPED_STEM -> true;
            default -> false;
        };
    }
}