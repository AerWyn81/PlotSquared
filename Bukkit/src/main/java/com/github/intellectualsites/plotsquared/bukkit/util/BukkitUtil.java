package com.github.intellectualsites.plotsquared.bukkit.util;

import com.github.intellectualsites.plotsquared.bukkit.object.BukkitPlayer;
import com.github.intellectualsites.plotsquared.plot.PlotSquared;
import com.github.intellectualsites.plotsquared.plot.config.Captions;
import com.github.intellectualsites.plotsquared.plot.object.Location;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import com.github.intellectualsites.plotsquared.plot.object.RunnableVal;
import com.github.intellectualsites.plotsquared.plot.object.schematic.PlotItem;
import com.github.intellectualsites.plotsquared.plot.util.MainUtil;
import com.github.intellectualsites.plotsquared.plot.util.MathMan;
import com.github.intellectualsites.plotsquared.plot.util.StringComparison;
import com.github.intellectualsites.plotsquared.plot.util.TaskManager;
import com.github.intellectualsites.plotsquared.plot.util.UUIDHandler;
import com.github.intellectualsites.plotsquared.plot.util.WorldUtil;
import com.github.intellectualsites.plotsquared.plot.util.world.BlockUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockState;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.List;
import java.util.Set;


@SuppressWarnings({"unused", "WeakerAccess"})
public class BukkitUtil extends WorldUtil {

    private static Method biomeSetter;
    static {
        try {
            biomeSetter = World.class.getMethod("setBiome", Integer.class, Integer.class, Biome.class);
        } catch (final Exception ignored) {
        }
    }

    private static String lastString = null;
    private static World lastWorld = null;

    private static Player lastPlayer = null;
    private static PlotPlayer lastPlotPlayer = null;

    public static void removePlayer(String player) {
        lastPlayer = null;
        lastPlotPlayer = null;
    }

    public static PlotPlayer getPlayer(@NonNull final OfflinePlayer op) {
        if (op.isOnline()) {
            return getPlayer(op.getPlayer());
        }
        final Player player = OfflinePlayerUtil.loadPlayer(op);
        player.loadData();
        return new BukkitPlayer(player, true);
    }

    /**
     * Get a plot based on the location.
     *
     * @param location the location to check
     * @return plot if found, otherwise it creates a temporary plot
     * @see Plot
     */
    public static Plot getPlot(org.bukkit.Location location) {
        if (location == null) {
            return null;
        }
        return getLocation(location).getPlot();
    }

    /**
     * Get a plot based on the player location.
     *
     * @param player the player to check
     * @return plot if found, otherwise it creates a temporary plot
     * @see #getPlot(org.bukkit.Location)
     * @see Plot
     */
    public static Plot getPlot(Player player) {
        return getPlot(player.getLocation());
    }

    /**
     * Get home location.
     *
     * @param plot Plot that you want to get the location for
     * @return plot bottom location
     * @see Plot
     */
    public static org.bukkit.Location getHomeLocation(Plot plot) {
        return BukkitUtil.getLocation(plot.getHome());
    }

    /**
     * Get the PlotPlayer for an offline player.
     *
     * <p>Note that this will work if the player is offline, however not all
     * functionality will work.
     *
     * @param player the player to wrap
     * @return a {@code PlotPlayer}
     * @see PlotPlayer#wrap(Object)
     */
    public static PlotPlayer wrapPlayer(OfflinePlayer player) {
        return PlotPlayer.wrap(player);
    }

    /**
     * Gets the PlotPlayer for a player. The PlotPlayer is usually cached and
     * will provide useful functions relating to players.
     *
     * @param player the player to wrap
     * @return a {@code PlotPlayer}
     * @see PlotPlayer#wrap(Object)
     */
    public static PlotPlayer wrapPlayer(Player player) {
        return PlotPlayer.wrap(player);
    }

    /**
     * Gets the PlotPlayer for a UUID. The PlotPlayer is usually cached and
     * will provide useful functions relating to players.
     *
     * @param uuid the uuid to wrap
     * @return a {@code PlotPlayer}
     * @see PlotPlayer#wrap(Object)
     */
    @Override public PlotPlayer wrapPlayer(UUID uuid) {
        return PlotPlayer.wrap(Bukkit.getOfflinePlayer(uuid));
    }

    /**
     * Gets the number of plots, which the player is able to build in.
     *
     * @param player player, for whom we're getting the plots
     * @return the number of allowed plots
     */
    public static int getAllowedPlots(Player player) {
        PlotPlayer plotPlayer = PlotPlayer.wrap(player);
        return plotPlayer.getAllowedPlots();
    }

    /**
     * Check whether or not a player is in a plot.
     *
     * @param player who we're checking for
     * @return true if the player is in a plot, false if not-
     */
    public static boolean isInPlot(Player player) {
        return getPlot(player) != null;
    }

    /**
     * Gets a collection containing the players plots.
     *
     * @param world  Specify the world we want to select the plots from
     * @param player Player, for whom we're getting the plots
     * @return a set containing the players plots
     * @see Plot
     */
    public static Set<Plot> getPlayerPlots(String world, Player player) {
        if (world == null) {
            return new HashSet<>();
        }
        return PlotPlayer.wrap(player).getPlots(world);
    }

    /**
     * Send a message to a player. The message supports color codes.
     *
     * @param player the recipient of the message
     * @param string the message
     * @see MainUtil#sendMessage(PlotPlayer, String)
     */
    public static void sendMessage(Player player, String string) {
        MainUtil.sendMessage(BukkitUtil.getPlayer(player), string);
    }

    /**
     * Gets the player plot count.
     *
     * @param world  Specify the world we want to select the plots from
     * @param player Player, for whom we're getting the plot count
     * @return the number of plots the player has
     */
    public static int getPlayerPlotCount(String world, Player player) {
        if (world == null) {
            return 0;
        }
        return BukkitUtil.getPlayer(player).getPlotCount(world);
    }

    /**
     * Send a message to a player.
     *
     * @param player  the recipient of the message
     * @param caption the message
     * @see MainUtil#sendMessage(com.github.intellectualsites.plotsquared.commands.CommandCaller, Captions, String...)
     */
    public static void sendMessage(Player player, Captions caption) {
        MainUtil.sendMessage(BukkitUtil.getPlayer(player), caption);
    }

    public static PlotPlayer getPlayer(@NonNull final Player player) {
        if (player == lastPlayer) {
            return lastPlotPlayer;
        }
        final String name = player.getName();
        final PlotPlayer plotPlayer = UUIDHandler.getPlayer(name);
        if (plotPlayer != null) {
            return plotPlayer;
        }
        lastPlotPlayer = new BukkitPlayer(player);
        UUIDHandler.getPlayers().put(name, lastPlotPlayer);
        lastPlayer = player;
        return lastPlotPlayer;
    }

    public static Location getLocation(@NonNull final org.bukkit.Location location) {
        return new Location(location.getWorld().getName(), MathMan.roundInt(location.getX()),
            MathMan.roundInt(location.getY()), MathMan.roundInt(location.getZ()));
    }

    public static Location getLocationFull(@NonNull final org.bukkit.Location location) {
        return new Location(location.getWorld().getName(), MathMan.roundInt(location.getX()),
            MathMan.roundInt(location.getY()), MathMan.roundInt(location.getZ()), location.getYaw(),
            location.getPitch());
    }

    public static org.bukkit.Location getLocation(@NonNull final Location location) {
        return new org.bukkit.Location(getWorld(location.getWorld()), location.getX(),
            location.getY(), location.getZ());
    }

    public static World getWorld(@NonNull final String string) {
        return Bukkit.getWorld(string);
    }

    public static String getWorld(@NonNull final Entity entity) {
        return entity.getWorld().getName();
    }

    public static List<Entity> getEntities(@NonNull final String worldName) {
        World world = getWorld(worldName);
        if (world != null) {
            return world.getEntities();
        } else {
            return new ArrayList<>();
        }
    }

    public static Location getLocation(@NonNull final Entity entity) {
        final org.bukkit.Location location = entity.getLocation();
        String world = location.getWorld().getName();
        return new Location(world, location.getBlockX(), location.getBlockY(),
            location.getBlockZ());
    }

    @NotNull public static Location getLocationFull(@NonNull final Entity entity) {
        final org.bukkit.Location location = entity.getLocation();
        return new Location(location.getWorld().getName(), MathMan.roundInt(location.getX()),
            MathMan.roundInt(location.getY()), MathMan.roundInt(location.getZ()), location.getYaw(),
            location.getPitch());
    }

    public static Material getMaterial(@NonNull final BlockState plotBlock) {
        return BukkitAdapter.adapt(plotBlock.getBlockType());
    }

    @Override public boolean isBlockSame(BlockState block1, BlockState block2) {
        if (block1.equals(block2)) {
            return true;
        }
        Material mat1 = getMaterial(block1), mat2 = getMaterial(block2);
        return mat1 == mat2;
    }

    @Override public boolean isWorld(@NonNull final String worldName) {
        return getWorld(worldName) != null;
    }

    @Override public BiomeType getBiome(String world, int x, int z) {
        return BukkitAdapter.adapt(getWorld(world).getBiome(x, z));
    }

    @Override public int getHighestBlock(@NonNull final String world, final int x, final int z) {
        final World bukkitWorld = getWorld(world);
        // Skip top and bottom block
        int air = 1;
        for (int y = bukkitWorld.getMaxHeight() - 1; y >= 0; y--) {
            Block block = bukkitWorld.getBlockAt(x, y, z);
            Material type = block.getType();
            if (type.isSolid()) {
                if (air > 1) {
                    return y;
                }
                air = 0;
            } else {
                if (block.isLiquid()) {
                    return y;
                }
                air++;
            }
        }
        return bukkitWorld.getMaxHeight() - 1;
    }

    @Override @Nullable public String[] getSign(@NonNull final Location location) {
        Block block = getWorld(location.getWorld())
            .getBlockAt(location.getX(), location.getY(), location.getZ());
        return TaskManager.IMP.sync(new RunnableVal<String[]>() {
            @Override public void run(String[] value) {
                if (block.getState() instanceof Sign) {
                    Sign sign = (Sign) block.getState();
                    this.value = sign.getLines();
                }
            }
        });
    }

    @Override public Location getSpawn(@NonNull final String world) {
        final org.bukkit.Location temp = getWorld(world).getSpawnLocation();
        return new Location(world, temp.getBlockX(), temp.getBlockY(), temp.getBlockZ(),
            temp.getYaw(), temp.getPitch());
    }

    @Override public void setSpawn(@NonNull final Location location) {
        final World world = getWorld(location.getWorld());
        if (world != null) {
            world.setSpawnLocation(location.getX(), location.getY(), location.getZ());
        }
    }

    @Override public void saveWorld(@NonNull final String worldName) {
        final World world = getWorld(worldName);
        if (world != null) {
            world.save();
        }
    }

    @Override @SuppressWarnings("deprecation")
    public void setSign(@NonNull final String worldName, final int x, final int y, final int z,
        @NonNull final String[] lines) {
        final World world = getWorld(worldName);
        final Block block = world.getBlockAt(x, y, z);
        //        block.setType(Material.AIR);
        final Material type = block.getType();
        if (type != Material.LEGACY_SIGN && type != Material.LEGACY_WALL_SIGN) {
            BlockFace facing = BlockFace.EAST;
            if (world.getBlockAt(x, y, z + 1).getType().isSolid()) {
                facing = BlockFace.NORTH;
            } else if (world.getBlockAt(x + 1, y, z).getType().isSolid()) {
                facing = BlockFace.WEST;
            } else if (world.getBlockAt(x, y, z - 1).getType().isSolid()) {
                facing = BlockFace.SOUTH;
            }
            if (PlotSquared.get().IMP.getServerVersion()[1] == 13) {
                block.setType(Material.valueOf("WALL_SIGN"), false);
            } else {
                block.setType(Material.valueOf("OAK_WALL_SIGN"), false);
            }
            if (!(block.getBlockData() instanceof WallSign)) {
                PlotSquared.debug(block.getBlockData().getAsString());
                throw new RuntimeException("Something went wrong generating a sign");
            }
            final Directional sign = (Directional) block.getBlockData();
            sign.setFacing(facing);
            block.setBlockData(sign, false);
        }
        final org.bukkit.block.BlockState blockstate = block.getState();
        if (blockstate instanceof Sign) {
            final Sign sign = (Sign) blockstate;
            for (int i = 0; i < lines.length; i++) {
                sign.setLine(i, lines[i]);
            }
            sign.update(true);
        }
    }

    @Override
    public boolean addItems(@NonNull final String worldName, @NonNull final PlotItem items) {
        final World world = getWorld(worldName);
        final Block block = world.getBlockAt(items.x, items.y, items.z);
        final org.bukkit.block.BlockState state = block.getState();
        if (state instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) state;
            Inventory inv = holder.getInventory();
            for (int i = 0; i < items.types.length; i++) {
                // ItemStack item = new ItemStack(LegacyMappings.fromLegacyId(items.id[i]).getMaterial(), items.amount[i], items.data[i]);
                ItemStack item =
                    new ItemStack(BukkitAdapter.adapt(items.types[i]), items.amount[i]);
                inv.addItem(item);
            }
            state.update(true);
            return true;
        }
        return false;
    }

    @Override public boolean isBlockSolid(@NonNull final BlockState block) {
        return block.getBlockType().getMaterial().isSolid();
    }

    @Override public String getClosestMatchingName(@NonNull final BlockState block) {
        try {
            return getMaterial(block).name();
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override @Nullable
    public StringComparison<BlockState>.ComparisonResult getClosestBlock(String name) {
        BlockState state = BlockUtil.get(name);
        return new StringComparison<BlockState>().new ComparisonResult(1, state);
    }

    @Override
    public void setBiomes(@NonNull final String worldName, @NonNull final CuboidRegion region,
        @NonNull final BiomeType biomeType) {
        final World world = getWorld(worldName);
        if (world == null) {
            PlotSquared.log("An error occurred setting the biome because the world was null.");
            return;
        }
        final Biome biome = BukkitAdapter.adapt(biomeType);
        for (int x = region.getMinimumPoint().getX(); x <= region.getMaximumPoint().getX(); x++) {
            for (int z = region.getMinimumPoint().getZ();
                 z <= region.getMaximumPoint().getZ(); z++) {
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    try {
                        if (biomeSetter != null) {
                            biomeSetter.invoke(world, x, z, biome);
                        } else {
                            world.setBiome(x, y, z, biome);
                        }
                    } catch (final Exception e) {
                        PlotSquared.log("An error occurred setting the biome:");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public com.sk89q.worldedit.world.World getWeWorld(String world) {
        return new BukkitWorld(Bukkit.getWorld(world));
    }

    @Override public BlockState getBlock(@NonNull final Location location) {
        final World world = getWorld(location.getWorld());
        final Block block = world.getBlockAt(location.getX(), location.getY(), location.getZ());
        return BukkitAdapter.asBlockType(block.getType()).getDefaultState();
    }

    @Override public String getMainWorld() {
        return Bukkit.getWorlds().get(0).getName();
    }

    @Override public double getHealth(PlotPlayer player) {
        return Bukkit.getPlayer(player.getUUID()).getHealth();
    }

    @Override public int getFoodLevel(PlotPlayer player) {
        return Bukkit.getPlayer(player.getUUID()).getFoodLevel();
    }

    @Override public void setHealth(PlotPlayer player, double health) {
        Bukkit.getPlayer(player.getUUID()).setHealth(health);
    }

    @Override public void setFoodLevel(PlotPlayer player, int foodLevel) {
        Bukkit.getPlayer(player.getUUID()).setFoodLevel(foodLevel);
    }
}
