package com.newestaf.enhancedenchantment.anvil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class RepairMaterial {

    private static final Map<Material, RecipeChoice> MATERIAL_TO_REPAIRABLE
            = new EnumMap<Material, RecipeChoice>(Material.class);

    public static boolean isRepairable(ItemStack base, ItemStack addition) {
        RecipeChoice recipeChoice = MATERIAL_TO_REPAIRABLE.get(base.getType());
        return recipeChoice != null && recipeChoice.test(addition);
    }

    static {
        String[] armor = new String[] { "_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS" };
        String[] tools = new String[] { "_AXE", "_SHOVEL", "_PICKAXE", "_HOE", "_SWORD" };
        String[] armortools = new String[armor.length + tools.length];
        System.arraycopy(armor, 0, armortools, 0, armor.length);
        System.arraycopy(tools, 0, armortools, armor.length, tools.length);

    }

    private static void addGear(String type, String[] gearType, RecipeChoice repairChoice) {
        for (String toolType : gearType) {
            Material material = Material.getMaterial(type + toolType);
            if (material != null) {
                MATERIAL_TO_REPAIRABLE.put(material, repairChoice);
            }
        }

    }

    private static void addGear(String type, String[] gearType, Material repairMaterial) {
        addGear(type, gearType, singleChoice(repairMaterial));
    }

    private static RecipeChoice singleChoice(Material material) {
        return new RecipeChoice.MaterialChoice(List.of(material));
    }

    @VisibleForTesting
    static boolean hasEntry(Material material) {
        return MATERIAL_TO_REPAIRABLE.containsKey(material);
    }

    private RepairMaterial() {}

}
