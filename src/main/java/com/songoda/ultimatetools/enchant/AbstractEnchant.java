package com.songoda.ultimatetools.enchant;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.nms.NmsManager;
import com.songoda.core.nms.nbt.NBTCore;
import com.songoda.core.nms.nbt.NBTItem;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractEnchant {

    private final String key;

    private final String name;

    private final int minLevel;

    private final int maxLevel;

    private final List<ToolType> applicableTypes = new ArrayList<>();

    public AbstractEnchant(String key, String name, int minLevel, int maxLevel, ToolType... toolTypes) {
        this.key = key;
        this.name = name;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        applicableTypes.addAll(Arrays.asList(toolTypes));
    }

    public void onInteract(PlayerInteractEvent event) {
    }

    public void onBlockBreak(BlockBreakEvent event) {
    }

    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event) {
    }

    public void onEntityKilledByPlayer(EntityDeathEvent event) {
    }

    public ItemStack apply(ItemStack item) {
        ItemMeta itemmeta = item.getItemMeta();
        List<String> lore = itemmeta.hasLore() ? itemmeta.getLore() : new ArrayList<>();

        NBTCore nbt = NmsManager.getNbt();
        NBTItem nbtItem = nbt.of(item);

        if (nbtItem.has("UTE")) {
            for (String key : nbtItem.getNBTObject("UTE").asString().split(";"))
                if (key.equals(this.key))
                    return item;
        }

        lore.add(TextUtils.formatText("&7" + name));

        itemmeta.setLore(lore);
        item.setItemMeta(itemmeta);

        ItemUtils.addGlow(item);
        nbtItem = nbt.of(item);

        if (nbtItem.has("UTE"))
            nbtItem.set("UTE", nbtItem.getNBTObject("UTE").asString() + ";" + key);
        else
            nbtItem.set("UTE", key);
        return nbtItem.finish();
    }

    public ItemStack getBook() {
        return getBook(minLevel);
    }

    public ItemStack getBook(int level) {
        if (level > maxLevel || level < minLevel)
            return null;

        ItemStack book = CompatibleMaterial.ENCHANTED_BOOK.getItem();
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(TextUtils.formatText("&eEnchanted Book"));

        ArrayList<String> lore = new ArrayList<>();
        lore.add(TextUtils.formatText("&7" + name));
        meta.setLore(lore);
        book.setItemMeta(meta);

        NBTCore nbt = NmsManager.getNbt();
        NBTItem nbtItem = nbt.of(book);
        nbtItem.set("UTE", key); // UltimateToolsEnchant

        return nbtItem.finish();
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public List<ToolType> getApplicableTypes() {
        return Collections.unmodifiableList(applicableTypes);
    }

    public boolean isApplicableType(CompatibleMaterial material) {
        for (ToolType type : applicableTypes) {
            for (CompatibleMaterial m : type.getMaterials()) {
                if (material == m) return true;
            }
        }
        return false;
    }
}