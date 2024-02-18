package me.rewu.euclidean_portals;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Candle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;

public class PortalsAPI {

    private static int PORTAL_WIDTH = 3;

    private static HashMap<Material, Integer> candlesValues = new HashMap<>() {{
       put(Material.PINK_CANDLE, 1);
       put(Material.ORANGE_CANDLE, 5);
       put(Material.RED_CANDLE, 10);
       put(Material.LIGHT_BLUE_CANDLE, 50);
       put(Material.CYAN_CANDLE, 100);
       put(Material.BLUE_CANDLE, 500);
       put(Material.LIGHT_GRAY_CANDLE, 1000);
       put(Material.GRAY_CANDLE, 5000);
       put(Material.BLACK_CANDLE, 10000);
    }};

    public static boolean checkPortal(Location center) {
        Block block = center.getBlock();

        for (int i=-PORTAL_WIDTH; i<=PORTAL_WIDTH; i++) {
            if (i == 0) continue;

            if (!(block.getRelative(i, 0, 0).getBlockData() instanceof Candle candleX) || !candleX.isLit()) return false;
            if (!(block.getRelative(0, 0, i).getBlockData() instanceof Candle candleZ) || !candleZ.isLit()) return false;
        }

        for (int i=-2; i <=2; i += 4)
        for (int j=-2; j <=2; j += 4)
            if (!(block.getRelative(0, 0, i).getBlockData() instanceof Candle candle) || !candle.isLit()) return false;

        litPortal(center);

        return true;
    }

    public static void litPortal(Location center) {
        World world = center.getWorld();

        center.setX(center.getBlockX() + 0.5);
        center.setZ(center.getBlockZ() + 0.5);

        Location destination = calculateCoordinates(center);

        BukkitScheduler scheduler = Bukkit.getScheduler();

        final double[] size = {PORTAL_WIDTH};
        double decrement = PORTAL_WIDTH / 20.0;

        int id = scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            for (int d = 0; d <= 90; d += 1) {
                Location pLoc = center.clone();
                pLoc.setX(center.getX() + Math.cos(d) * size[0]);
                pLoc.setZ(center.getZ() + Math.sin(d) * size[0]);

                world.spawnParticle(Particle.REVERSE_PORTAL, pLoc, 1, 0, 0, 0, 0);
            }

            size[0] -= decrement;
        }, 0L, 5L);

        scheduler.scheduleSyncDelayedTask(Main.getInstance(), () -> {
            scheduler.cancelTask(id);

            world.spawnParticle(Particle.REVERSE_PORTAL, center, 50, 0, 0, 0);

            world.getNearbyEntities(center, 3, 2, 3).forEach(entity -> entity.teleport(destination));
        }, 5 * 20L);
    }

    private static Location calculateCoordinates(Location center) {
        World world = center.getWorld();
        Block block = center.getBlock();

        int x = center.getBlockX(), z = center.getBlockZ();

        for (int i=-PORTAL_WIDTH; i<=PORTAL_WIDTH; i++) {
            if (i == 0) continue;

            x += Math.signum(i) * getCandleValue((Candle) block.getRelative(i, 0, 0).getBlockData());
            z += Math.signum(i) * getCandleValue((Candle) block.getRelative(0, 0, i).getBlockData());
        }

        Location loc = world.getHighestBlockAt(x, z).getLocation();
        loc.setX(loc.getX() + 0.5);
        loc.setY(loc.getY() + 1);
        loc.setZ(loc.getZ() + 0.5);

        return loc;
    }

    public static Integer getCandleValue(Material material) {
        if (!candlesValues.containsKey(material)) return 0;

        return candlesValues.get(material);
    }

    public static Integer getCandleValue(Candle candle) {
        return getCandleValue(candle.getMaterial()) * candle.getCandles();
    }
}
