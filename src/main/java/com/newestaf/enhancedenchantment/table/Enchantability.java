package com.newestaf.enhancedenchantment.table;

import org.bukkit.Material;

import java.util.EnumMap;
import java.util.Map;

public record Enchantability(int value) {

    public static final Enchantability LEATHER;
    public static final Enchantability CHAIN;
    public static final Enchantability IRON_ARMOR;
    public static final Enchantability GOLD_ARMOR;
    public static final Enchantability DIAMOND;
    public static final Enchantability TURTLE;
    public static final Enchantability NETHERITE;
    public static final Enchantability WOOD;
    public static final Enchantability STONE;
    public static final Enchantability IRON_TOOL;
    public static final Enchantability GOLD_TOOL;
    public static final Enchantability BOOK;
    public static final Enchantability TRIDENT;

    public static Enchantability forMaterial(Material material) {
        return BY_MATERIAL.get(material);
    }

    private static final Map<Material, Enchantability> BY_MATERIAL = new EnumMap<>(Material.class);

    static {
        String[] armor = new String[] { "_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS" };
        LEATHER = addMaterials("LEATHER", armor, 15);
        CHAIN = addMaterials("CHAINMAIL", armor, 12);
        IRON_ARMOR = addMaterials("IRON", armor, 9);
        TURTLE = addMaterial(Material.TURTLE_HELMET, IRON_ARMOR);
        GOLD_ARMOR = addMaterials("GOLDEN", armor, 25);

        String[] tools = new String[] { "_AXE", "_SHOVEL", "_PICKAXE", "_HOE", "_SWORD" };
        WOOD = addMaterials("WOODEN", tools, LEATHER);
        addMaterial(Material.SHIELD, WOOD);
        addMaterial(Material.BOW, WOOD);
        addMaterial(Material.FISHING_ROD, WOOD);
        addMaterial(Material.CROSSBOW, WOOD);
        STONE = addMaterials("STONE", tools, 5);
        IRON_TOOL = addMaterials("IRON", tools, 14);
        GOLD_TOOL = addMaterials("GOLDEN", tools, 22);

        String[] armortools = new String[armor.length + tools.length];
        System.arraycopy(armor, 0, armortools, 0, armor.length);
        System.arraycopy(tools, 0, armortools, armor.length, tools.length);
        DIAMOND = addMaterials("DIAMOND", armortools, 10);
        NETHERITE = addMaterials("NETHERITE", armortools, LEATHER);

        BOOK = addMaterial(Material.BOOK, new Enchantability(1));
        BY_MATERIAL.put(Material.ENCHANTED_BOOK, BOOK);
        TRIDENT = addMaterial(Material.TRIDENT, BOOK);
    }

    private static Enchantability addMaterial(
            Material material,
            Enchantability enchantability
    ) {
        BY_MATERIAL.put(material, enchantability);
        return enchantability;

    }

    private static Enchantability addMaterials(
            String materialName,
            String[] gearType,
            int value
    ) {
        return addMaterials(materialName, gearType, new Enchantability(value));
    }

    private static Enchantability addMaterials(
            String materialName,
            String[] gearType,
            Enchantability value
    ) {
        for (String toolType : gearType) {
            Material material = Material.getMaterial(materialName + toolType);
            if (material != null) {
                BY_MATERIAL.put(material, value);
            }
        }
        return value;
    }
}
