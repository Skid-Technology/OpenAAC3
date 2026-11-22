package me.konsolas.aac.gui;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class MenuItem {
    private static final String[] EMPTY_LORE = new String[0];
    protected String displayName = "this_is_a_default_value";
    protected Object[] formatArguments;
    protected String[] lore = EMPTY_LORE;
    protected int slot = 0;
    protected ItemStack icon = new ItemStack(Material.STONE);
    protected boolean cancelClick = true;
    private int hotbarKey = -1;

    public ItemStack buildIcon() {
        ItemMeta itemMeta = this.icon.getItemMeta();
        if (!this.displayName.equals("this_is_a_default_value")) {
            itemMeta.setDisplayName(this.displayName);
        }
        if (this.lore.length != EMPTY_LORE.length) {
            itemMeta.setLore(Arrays.asList(this.lore));
        }
        this.icon.setItemMeta(itemMeta);
        return this.icon;
    }

    public MenuItem setMaterial(Material material) {
        this.icon = new ItemStack(material);
        return this;
    }

    public MenuItem setLore(List<String> list) {
        this.lore = list.toArray(new String[0]);
        return this;
    }

    public MenuItem setLore(String[] stringArray) {
        this.lore = stringArray;
        return this;
    }

    public MenuItem setFormattedName(String str, Object[] objectArray) {
        this.displayName = str;
        this.formatArguments = objectArray;
        return this;
    }

    public MenuItem setDisplayName(String str) {
        this.displayName = str;
        this.formatArguments = new Object[0];
        return this;
    }

    public abstract void onClick(Player player);

    public String getDisplayName() {
        return this.displayName;
    }

    public int getSlot() {
        return this.slot;
    }

    public MenuItem setSlot(int i) {
        this.slot = i;
        return this;
    }

    public boolean shouldCancelClick() {
        return this.cancelClick;
    }

    public MenuItem setCancelClick(boolean flag) {
        this.cancelClick = flag;
        return this;
    }

    public int getHotbarKey() {
        return this.hotbarKey;
    }

    public MenuItem setHotbarKey(int i) {
        this.hotbarKey = i;
        return this;
    }

    public MenuItem setIcon(ItemStack itemStack) {
        this.icon = itemStack;
        return this;
    }

    public Object[] getFormatArguments() {
        return this.formatArguments;
    }

    public String[] getLore() {
        return this.lore;
    }

}

