package me.konsolas.aac.util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullItemBuilder
extends ItemStack {
    public static final SkullItemBuilder NO_PERMISSION_ICON;
    private String skullOwnerName;

    public SkullItemBuilder() {
        super(Material.AIR);
    }

    public SkullItemBuilder(Material material) {
        super(material);
    }

    public SkullItemBuilder(Material material, int i) {
        super(material, i);
    }

    public SkullItemBuilder(Material material, int i, short s) {
        super(material, i, s);
    }

    public SkullItemBuilder(SkullItemBuilder skullItemBuilder) {
        super((ItemStack)skullItemBuilder);
    }

    public SkullItemBuilder withDisplayName(String str) {
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)str));
        this.setItemMeta(itemMeta);
        return this;
    }

    public SkullItemBuilder withLore(String[] stringArray) {
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setLore(Arrays.asList(stringArray));
        this.setItemMeta(itemMeta);
        return this;
    }

    public SkullItemBuilder withLore(List list) {
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setLore(list);
        this.setItemMeta(itemMeta);
        return this;
    }

    public SkullItemBuilder withEnchantment(Enchantment enchantment, int i) {
        this.addUnsafeEnchantment(enchantment, i);
        return this;
    }

    public SkullItemBuilder withLeatherColor(Color color) {
        if (!this.getType().name().startsWith("LEATHER")) {
            throw new IllegalArgumentException("Material has to be a leather armor");
        }
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)this.getItemMeta();
        leatherArmorMeta.setColor(color);
        this.setItemMeta((ItemMeta)leatherArmorMeta);
        return this;
    }

    public SkullItemBuilder withFlags(ItemFlag[] itemFlagArray) {
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.addItemFlags(itemFlagArray);
        this.setItemMeta(itemMeta);
        return this;
    }

    public SkullItemBuilder withoutFlags(ItemFlag[] itemFlagArray) {
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.removeItemFlags(itemFlagArray);
        this.setItemMeta(itemMeta);
        return this;
    }

    public Set getFlags() {
        return this.getItemMeta().getItemFlags();
    }

    public boolean hasFlag(ItemFlag itemFlag) {
        return this.getItemMeta().hasItemFlag(itemFlag);
    }

    public String getDisplayName() {
        return this.getItemMeta().getDisplayName();
    }

    public List getLoreLines() {
        return this.getItemMeta().hasLore() ? this.getItemMeta().getLore() : null;
    }

    public SkullItemBuilder copyBuilder() {
        return new SkullItemBuilder(this);
    }

    public SkullItemBuilder withSkullOwner(String str) {
        if (this.getType() != Material.SKULL_ITEM) {
            throw new IllegalArgumentException("Material has to be a skull item");
        }
        try {
            SkullMeta skullMeta = (SkullMeta)this.getItemMeta();
            skullMeta.setOwner(str);
            this.setItemMeta((ItemMeta)skullMeta);
        }
        catch (Throwable throwable) {
            Bukkit.getLogger().warning("[AAC] Could not receive skull owner for " + str);
        }
        return this;
    }

    public ItemStack clone() {
        return this.copyBuilder();
    }

    static {
        NO_PERMISSION_ICON = new SkullItemBuilder(Material.STAINED_GLASS_PANE, 1, (short)7);
    }
}

