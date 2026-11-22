package me.konsolas.aac.compat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;

public class ServerVersionAccess {
    private final Set blockingMaterials = EnumSet.noneOf(Material.class);
    private final Set useMaterials = EnumSet.noneOf(Material.class);
    private final ServerVersion version;
    private final String versionPackage;
    private Method getHandleMethod;
    private Method getFlagMethod;

    public ServerVersionAccess(String str) {
        if (str.contains("1.10")) {
            this.version = ServerVersion.VER_1_10;
            this.register1_10Materials();
            this.register1_9Materials();
            this.registerBaseMaterials();
        } else if (str.contains("1.9")) {
            this.version = ServerVersion.VER_1_9;
            this.register1_9Materials();
            this.registerBaseMaterials();
        } else if (str.contains("1.8.8")) {
            this.version = ServerVersion.VER_1_8;
            this.registerBaseMaterials();
        } else if (str.contains("1.11")) {
            this.version = ServerVersion.VER_1_11;
            this.register1_11Materials();
            this.register1_10Materials();
            this.register1_9Materials();
            this.registerBaseMaterials();
        } else if (str.contains("1.12")) {
            this.version = ServerVersion.VER_1_12;
            this.register1_12Materials();
            this.register1_11Materials();
            this.register1_10Materials();
            this.register1_9Materials();
            this.registerBaseMaterials();
        } else {
            Bukkit.getLogger().severe("[AAC] FATAL: FAILED TO AUTOCONFIGURE SERVER VERSION");
            Bukkit.getLogger().severe("[AAC] Translation: You modded your server so hard we can't even find its version number.");
            Server server = Bukkit.getServer();
            String str2 = "AAC";
            PluginManager pluginManager = Bukkit.getPluginManager();
            Plugin plugin = pluginManager.getPlugin(str2);
            PluginManager pluginManager2 = server.getPluginManager();
            pluginManager2.disablePlugin(plugin);
            throw new IllegalStateException("Unrecognised bukkit version.");
        }
        this.versionPackage = Bukkit.getServer().getClass().getName().split("\\.")[3];
    }

    private void register1_10Materials() {
        this.useMaterials.add(Material.MAGMA);
        this.useMaterials.add(Material.RED_NETHER_BRICK);
        this.useMaterials.add(Material.BONE_BLOCK);
        this.useMaterials.add(Material.NETHER_WART_BLOCK);
        this.blockingMaterials.add(Material.MAGMA);
        this.blockingMaterials.add(Material.RED_NETHER_BRICK);
        this.blockingMaterials.add(Material.BONE_BLOCK);
        this.blockingMaterials.add(Material.NETHER_WART_BLOCK);
    }

    private void register1_9Materials() {
        this.useMaterials.add(Material.PURPUR_BLOCK);
        this.useMaterials.add(Material.PURPUR_PILLAR);
        this.useMaterials.add(Material.PURPUR_DOUBLE_SLAB);
        this.useMaterials.add(Material.END_BRICKS);
        this.useMaterials.add(Material.FROSTED_ICE);
        this.blockingMaterials.add(Material.END_ROD);
        this.blockingMaterials.add(Material.CHORUS_PLANT);
        this.blockingMaterials.add(Material.CHORUS_FLOWER);
        this.blockingMaterials.add(Material.PURPUR_BLOCK);
        this.blockingMaterials.add(Material.PURPUR_DOUBLE_SLAB);
        this.blockingMaterials.add(Material.PURPUR_PILLAR);
        this.blockingMaterials.add(Material.PURPUR_SLAB);
        this.blockingMaterials.add(Material.PURPUR_STAIRS);
        this.blockingMaterials.add(Material.END_BRICKS);
        this.blockingMaterials.add(Material.END_GATEWAY);
        this.blockingMaterials.add(Material.GRASS_PATH);
        this.blockingMaterials.add(Material.FROSTED_ICE);
    }

    private void registerBaseMaterials() {
        this.useMaterials.add(Material.BARRIER);
        this.useMaterials.add(Material.BEACON);
        this.useMaterials.add(Material.BEDROCK);
        this.useMaterials.add(Material.BOOKSHELF);
        this.useMaterials.add(Material.BRICK);
        this.useMaterials.add(Material.BURNING_FURNACE);
        this.useMaterials.add(Material.CLAY);
        this.useMaterials.add(Material.CLAY_BRICK);
        this.useMaterials.add(Material.COAL_BLOCK);
        this.useMaterials.add(Material.COAL_ORE);
        this.useMaterials.add(Material.COBBLESTONE);
        this.useMaterials.add(Material.COMMAND);
        this.useMaterials.add(Material.DIAMOND_BLOCK);
        this.useMaterials.add(Material.DIAMOND_ORE);
        this.useMaterials.add(Material.DIRT);
        this.useMaterials.add(Material.DISPENSER);
        this.useMaterials.add(Material.DOUBLE_STEP);
        this.useMaterials.add(Material.DOUBLE_STONE_SLAB2);
        this.useMaterials.add(Material.DROPPER);
        this.useMaterials.add(Material.EMERALD_BLOCK);
        this.useMaterials.add(Material.EMERALD_ORE);
        this.useMaterials.add(Material.FURNACE);
        this.useMaterials.add(Material.GLASS);
        this.useMaterials.add(Material.GLOWING_REDSTONE_ORE);
        this.useMaterials.add(Material.GLOWSTONE);
        this.useMaterials.add(Material.GOLD_BLOCK);
        this.useMaterials.add(Material.GOLD_ORE);
        this.useMaterials.add(Material.GRASS);
        this.useMaterials.add(Material.GRAVEL);
        this.useMaterials.add(Material.HARD_CLAY);
        this.useMaterials.add(Material.HAY_BLOCK);
        this.useMaterials.add(Material.HUGE_MUSHROOM_1);
        this.useMaterials.add(Material.HUGE_MUSHROOM_2);
        this.useMaterials.add(Material.ICE);
        this.useMaterials.add(Material.IRON_BLOCK);
        this.useMaterials.add(Material.IRON_ORE);
        this.useMaterials.add(Material.JACK_O_LANTERN);
        this.useMaterials.add(Material.JUKEBOX);
        this.useMaterials.add(Material.LAPIS_BLOCK);
        this.useMaterials.add(Material.LAPIS_ORE);
        this.useMaterials.add(Material.LEAVES);
        this.useMaterials.add(Material.LEAVES_2);
        this.useMaterials.add(Material.LOG);
        this.useMaterials.add(Material.LOG_2);
        this.useMaterials.add(Material.MOB_SPAWNER);
        this.useMaterials.add(Material.MOSSY_COBBLESTONE);
        this.useMaterials.add(Material.MYCEL);
        this.useMaterials.add(Material.NETHERRACK);
        this.useMaterials.add(Material.NETHER_BRICK);
        this.useMaterials.add(Material.NETHER_BRICK_ITEM);
        this.useMaterials.add(Material.NOTE_BLOCK);
        this.useMaterials.add(Material.OBSIDIAN);
        this.useMaterials.add(Material.PACKED_ICE);
        this.useMaterials.add(Material.PRISMARINE);
        this.useMaterials.add(Material.PUMPKIN);
        this.useMaterials.add(Material.QUARTZ_BLOCK);
        this.useMaterials.add(Material.QUARTZ_ORE);
        this.useMaterials.add(Material.REDSTONE_BLOCK);
        this.useMaterials.add(Material.REDSTONE_LAMP_OFF);
        this.useMaterials.add(Material.REDSTONE_LAMP_ON);
        this.useMaterials.add(Material.REDSTONE_ORE);
        this.useMaterials.add(Material.RED_SANDSTONE);
        this.useMaterials.add(Material.SAND);
        this.useMaterials.add(Material.SANDSTONE);
        this.useMaterials.add(Material.SEA_LANTERN);
        this.useMaterials.add(Material.SMOOTH_BRICK);
        this.useMaterials.add(Material.SNOW_BLOCK);
        this.useMaterials.add(Material.SPONGE);
        this.useMaterials.add(Material.STAINED_CLAY);
        this.useMaterials.add(Material.STAINED_GLASS);
        this.useMaterials.add(Material.STONE);
        this.useMaterials.add(Material.TNT);
        this.useMaterials.add(Material.WOOD);
        this.useMaterials.add(Material.WOOD_DOUBLE_STEP);
        this.useMaterials.add(Material.WOOL);
        this.useMaterials.add(Material.WORKBENCH);
        this.blockingMaterials.add(Material.STONE);
        this.blockingMaterials.add(Material.GRASS);
        this.blockingMaterials.add(Material.DIRT);
        this.blockingMaterials.add(Material.COBBLESTONE);
        this.blockingMaterials.add(Material.WOOD);
        this.blockingMaterials.add(Material.BEDROCK);
        this.blockingMaterials.add(Material.SAND);
        this.blockingMaterials.add(Material.GRAVEL);
        this.blockingMaterials.add(Material.GOLD_ORE);
        this.blockingMaterials.add(Material.IRON_ORE);
        this.blockingMaterials.add(Material.COAL_ORE);
        this.blockingMaterials.add(Material.LOG);
        this.blockingMaterials.add(Material.LEAVES);
        this.blockingMaterials.add(Material.SPONGE);
        this.blockingMaterials.add(Material.GLASS);
        this.blockingMaterials.add(Material.LAPIS_ORE);
        this.blockingMaterials.add(Material.LAPIS_BLOCK);
        this.blockingMaterials.add(Material.DISPENSER);
        this.blockingMaterials.add(Material.SANDSTONE);
        this.blockingMaterials.add(Material.NOTE_BLOCK);
        this.blockingMaterials.add(Material.BED_BLOCK);
        this.blockingMaterials.add(Material.PISTON_STICKY_BASE);
        this.blockingMaterials.add(Material.PISTON_BASE);
        this.blockingMaterials.add(Material.PISTON_EXTENSION);
        this.blockingMaterials.add(Material.WOOL);
        this.blockingMaterials.add(Material.PISTON_MOVING_PIECE);
        this.blockingMaterials.add(Material.GOLD_BLOCK);
        this.blockingMaterials.add(Material.IRON_BLOCK);
        this.blockingMaterials.add(Material.DOUBLE_STEP);
        this.blockingMaterials.add(Material.STEP);
        this.blockingMaterials.add(Material.BRICK);
        this.blockingMaterials.add(Material.TNT);
        this.blockingMaterials.add(Material.BOOKSHELF);
        this.blockingMaterials.add(Material.MOSSY_COBBLESTONE);
        this.blockingMaterials.add(Material.OBSIDIAN);
        this.blockingMaterials.add(Material.MOB_SPAWNER);
        this.blockingMaterials.add(Material.WOOD_STAIRS);
        this.blockingMaterials.add(Material.CHEST);
        this.blockingMaterials.add(Material.DIAMOND_ORE);
        this.blockingMaterials.add(Material.DIAMOND_BLOCK);
        this.blockingMaterials.add(Material.WORKBENCH);
        this.blockingMaterials.add(Material.SOIL);
        this.blockingMaterials.add(Material.FURNACE);
        this.blockingMaterials.add(Material.BURNING_FURNACE);
        this.blockingMaterials.add(Material.WOODEN_DOOR);
        this.blockingMaterials.add(Material.COBBLESTONE_STAIRS);
        this.blockingMaterials.add(Material.IRON_DOOR_BLOCK);
        this.blockingMaterials.add(Material.REDSTONE_ORE);
        this.blockingMaterials.add(Material.GLOWING_REDSTONE_ORE);
        this.blockingMaterials.add(Material.ICE);
        this.blockingMaterials.add(Material.SNOW_BLOCK);
        this.blockingMaterials.add(Material.CACTUS);
        this.blockingMaterials.add(Material.CLAY);
        this.blockingMaterials.add(Material.JUKEBOX);
        this.blockingMaterials.add(Material.FENCE);
        this.blockingMaterials.add(Material.PUMPKIN);
        this.blockingMaterials.add(Material.NETHERRACK);
        this.blockingMaterials.add(Material.SOUL_SAND);
        this.blockingMaterials.add(Material.GLOWSTONE);
        this.blockingMaterials.add(Material.JACK_O_LANTERN);
        this.blockingMaterials.add(Material.CAKE_BLOCK);
        this.blockingMaterials.add(Material.STAINED_GLASS);
        this.blockingMaterials.add(Material.TRAP_DOOR);
        this.blockingMaterials.add(Material.MONSTER_EGGS);
        this.blockingMaterials.add(Material.SMOOTH_BRICK);
        this.blockingMaterials.add(Material.HUGE_MUSHROOM_1);
        this.blockingMaterials.add(Material.HUGE_MUSHROOM_2);
        this.blockingMaterials.add(Material.IRON_FENCE);
        this.blockingMaterials.add(Material.THIN_GLASS);
        this.blockingMaterials.add(Material.MELON_BLOCK);
        this.blockingMaterials.add(Material.FENCE_GATE);
        this.blockingMaterials.add(Material.BRICK_STAIRS);
        this.blockingMaterials.add(Material.SMOOTH_STAIRS);
        this.blockingMaterials.add(Material.MYCEL);
        this.blockingMaterials.add(Material.NETHER_BRICK);
        this.blockingMaterials.add(Material.NETHER_FENCE);
        this.blockingMaterials.add(Material.NETHER_BRICK_STAIRS);
        this.blockingMaterials.add(Material.ENCHANTMENT_TABLE);
        this.blockingMaterials.add(Material.BREWING_STAND);
        this.blockingMaterials.add(Material.CAULDRON);
        this.blockingMaterials.add(Material.ENDER_PORTAL_FRAME);
        this.blockingMaterials.add(Material.ENDER_STONE);
        this.blockingMaterials.add(Material.DRAGON_EGG);
        this.blockingMaterials.add(Material.REDSTONE_LAMP_OFF);
        this.blockingMaterials.add(Material.REDSTONE_LAMP_ON);
        this.blockingMaterials.add(Material.WOOD_DOUBLE_STEP);
        this.blockingMaterials.add(Material.WOOD_STEP);
        this.blockingMaterials.add(Material.SANDSTONE_STAIRS);
        this.blockingMaterials.add(Material.EMERALD_ORE);
        this.blockingMaterials.add(Material.ENDER_CHEST);
        this.blockingMaterials.add(Material.EMERALD_BLOCK);
        this.blockingMaterials.add(Material.SPRUCE_WOOD_STAIRS);
        this.blockingMaterials.add(Material.BIRCH_WOOD_STAIRS);
        this.blockingMaterials.add(Material.JUNGLE_WOOD_STAIRS);
        this.blockingMaterials.add(Material.COMMAND);
        this.blockingMaterials.add(Material.BEACON);
        this.blockingMaterials.add(Material.COBBLE_WALL);
        this.blockingMaterials.add(Material.ANVIL);
        this.blockingMaterials.add(Material.TRAPPED_CHEST);
        this.blockingMaterials.add(Material.DAYLIGHT_DETECTOR);
        this.blockingMaterials.add(Material.REDSTONE_BLOCK);
        this.blockingMaterials.add(Material.QUARTZ_ORE);
        this.blockingMaterials.add(Material.HOPPER);
        this.blockingMaterials.add(Material.QUARTZ_BLOCK);
        this.blockingMaterials.add(Material.QUARTZ_STAIRS);
        this.blockingMaterials.add(Material.DROPPER);
        this.blockingMaterials.add(Material.STAINED_CLAY);
        this.blockingMaterials.add(Material.HAY_BLOCK);
        this.blockingMaterials.add(Material.HARD_CLAY);
        this.blockingMaterials.add(Material.COAL_BLOCK);
        this.blockingMaterials.add(Material.STAINED_GLASS_PANE);
        this.blockingMaterials.add(Material.LEAVES_2);
        this.blockingMaterials.add(Material.LOG_2);
        this.blockingMaterials.add(Material.ACACIA_STAIRS);
        this.blockingMaterials.add(Material.DARK_OAK_STAIRS);
        this.blockingMaterials.add(Material.PACKED_ICE);
        this.blockingMaterials.add(Material.RED_SANDSTONE);
        this.blockingMaterials.add(Material.SLIME_BLOCK);
        this.blockingMaterials.add(Material.BARRIER);
        this.blockingMaterials.add(Material.IRON_TRAPDOOR);
        this.blockingMaterials.add(Material.PRISMARINE);
        this.blockingMaterials.add(Material.SEA_LANTERN);
        this.blockingMaterials.add(Material.DOUBLE_STONE_SLAB2);
        this.blockingMaterials.add(Material.RED_SANDSTONE_STAIRS);
        this.blockingMaterials.add(Material.STONE_SLAB2);
        this.blockingMaterials.add(Material.SPRUCE_FENCE_GATE);
        this.blockingMaterials.add(Material.BIRCH_FENCE_GATE);
        this.blockingMaterials.add(Material.JUNGLE_FENCE_GATE);
        this.blockingMaterials.add(Material.DARK_OAK_FENCE_GATE);
        this.blockingMaterials.add(Material.ACACIA_FENCE_GATE);
        this.blockingMaterials.add(Material.SPRUCE_FENCE);
        this.blockingMaterials.add(Material.BIRCH_FENCE);
        this.blockingMaterials.add(Material.JUNGLE_FENCE);
        this.blockingMaterials.add(Material.DARK_OAK_FENCE);
        this.blockingMaterials.add(Material.ACACIA_FENCE);
        this.blockingMaterials.add(Material.DAYLIGHT_DETECTOR_INVERTED);
        this.blockingMaterials.add(Material.SPRUCE_DOOR);
        this.blockingMaterials.add(Material.BIRCH_DOOR);
        this.blockingMaterials.add(Material.JUNGLE_DOOR);
        this.blockingMaterials.add(Material.ACACIA_DOOR);
        this.blockingMaterials.add(Material.DARK_OAK_DOOR);
        this.blockingMaterials.add(Material.LADDER);
        this.blockingMaterials.add(Material.VINE);
        this.blockingMaterials.add(Material.WATER_LILY);
        this.blockingMaterials.add(Material.SKULL);
        this.blockingMaterials.add(Material.FLOWER_POT);
        this.blockingMaterials.add(Material.COCOA);
    }

    private void register1_11Materials() {
        this.blockingMaterials.add(Material.OBSERVER);
        this.blockingMaterials.add(Material.WHITE_SHULKER_BOX);
        this.blockingMaterials.add(Material.ORANGE_SHULKER_BOX);
        this.blockingMaterials.add(Material.MAGENTA_SHULKER_BOX);
        this.blockingMaterials.add(Material.LIGHT_BLUE_SHULKER_BOX);
        this.blockingMaterials.add(Material.YELLOW_SHULKER_BOX);
        this.blockingMaterials.add(Material.LIME_SHULKER_BOX);
        this.blockingMaterials.add(Material.PINK_SHULKER_BOX);
        this.blockingMaterials.add(Material.GRAY_SHULKER_BOX);
        this.blockingMaterials.add(Material.SILVER_SHULKER_BOX);
        this.blockingMaterials.add(Material.CYAN_SHULKER_BOX);
        this.blockingMaterials.add(Material.PURPLE_SHULKER_BOX);
        this.blockingMaterials.add(Material.BLUE_SHULKER_BOX);
        this.blockingMaterials.add(Material.BROWN_SHULKER_BOX);
        this.blockingMaterials.add(Material.GREEN_SHULKER_BOX);
        this.blockingMaterials.add(Material.RED_SHULKER_BOX);
        this.blockingMaterials.add(Material.BLACK_SHULKER_BOX);
        this.blockingMaterials.add(Material.TOTEM);
        this.blockingMaterials.add(Material.SHULKER_SHELL);
    }

    private void register1_12Materials() {
        this.useMaterials.add(Material.BLACK_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.WHITE_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.ORANGE_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.MAGENTA_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.YELLOW_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.LIME_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.PINK_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.GRAY_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.SILVER_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.CYAN_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.PURPLE_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.BLUE_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.BROWN_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.GREEN_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.RED_GLAZED_TERRACOTTA);
        this.useMaterials.add(Material.CONCRETE);
        this.blockingMaterials.add(Material.BLACK_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.WHITE_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.ORANGE_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.MAGENTA_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.YELLOW_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.LIME_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.PINK_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.GRAY_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.SILVER_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.CYAN_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.PURPLE_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.BLUE_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.BROWN_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.GREEN_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.RED_GLAZED_TERRACOTTA);
        this.blockingMaterials.add(Material.CONCRETE);
    }

    public ServerVersion getVersion() {
        return this.version;
    }

    public boolean isSolidUseMaterial(Material material) {
        return this.useMaterials.contains(material);
    }

    public boolean isBlockingMaterial(Material material) {
        return this.blockingMaterials.contains(material);
    }

    public boolean hasLevitation(Player player) {
        return this.version != ServerVersion.VER_1_8 && player.hasPotionEffect(PotionEffectType.LEVITATION);
    }

    public boolean isElytraGliding(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Must be called from the main thread");
        }
        if (this.version != ServerVersion.VER_1_8) {
            try {
                if (this.getHandleMethod == null) {
                    this.getHandleMethod = player.getClass().getDeclaredMethod("getHandle", new Class[0]);
                    this.getHandleMethod.setAccessible(true);
                }
                Object obj = this.getHandleMethod.invoke(player, new Object[0]);
                if (this.getFlagMethod == null) {
                    this.getFlagMethod = obj.getClass().getMethod("getFlag", Integer.TYPE);
                    this.getFlagMethod.setAccessible(true);
                }
                return (Boolean)this.getFlagMethod.invoke(obj, 7);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return false;
    }

    public List<ItemStack> getHeldItems(Player player) {
        if (this.version == ServerVersion.VER_1_8) {
            ItemStack itemStack = player.getItemInHand();
            return Collections.singletonList(itemStack);
        }
        return Arrays.asList(player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand());
    }

    public boolean hasConsumableOrWeapon(List list) {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = (ItemStack)iterator.next();
            if (itemStack.getType().isEdible()) {
                return true;
            }
            if (this.version == ServerVersion.VER_1_8) {
                switch (itemStack.getType()) {
                    case STONE_SWORD: 
                    case DIAMOND_SWORD: 
                    case IRON_SWORD: 
                    case GOLD_SWORD: 
                    case WOOD_SWORD: 
                    case BOW: {
                        return true;
                    }
                }
                continue;
            }
            switch (itemStack.getType()) {
                case BOW: 
                case SHIELD: {
                    return true;
                }
            }
        }
        return false;
    }

    public float getAttackCooldownTicks(Player player) {
        if (this.version == ServerVersion.VER_1_8) {
            return 10.0f;
        }
        return (float)(1.0 / player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue() * 20.0);
    }

    public String getVersionPackage() {
        return this.versionPackage;
    }

}

