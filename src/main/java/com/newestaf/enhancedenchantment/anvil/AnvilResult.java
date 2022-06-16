package com.newestaf.enhancedenchantment.anvil;

import com.newestaf.enhancedenchantment.util.ItemUtil;
import org.bukkit.inventory.ItemStack;

public record AnvilResult(ItemStack item, int levelCost, int materialCost) {

    public static final AnvilResult EMPTY = new AnvilResult(ItemUtil.AIR, 0, 0);

}
