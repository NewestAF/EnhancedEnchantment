package com.newestaf.enhancedenchantment.anvil;

import com.newestaf.enhancedenchantment.util.ItemUtil;
import com.newestaf.enhancedenchantment.util.MetaCachedStack;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.Objects;

public interface AnvilFunction {

    AnvilFunction PRIOR_WORK_LEVEL_COST = new AnvilFunction() {
        @Override
        public boolean canApply(AnvilOperation operation, AnvilOperationState state) {
            return true;
        }

        @Override
        public AnvilFunctionResult getResult(AnvilOperation operation, AnvilOperationState state) {
            return new AnvilFunctionResult() {
                @Override
                public int getLevelCostIncrease() {
                    return ItemUtil.getRepairCost(state.getBase().getMeta())
                            + ItemUtil.getRepairCost(state.getAddition().getMeta());
                }
            };
        }
    };

    AnvilFunction RENAME = new AnvilFunction() {
        @Override
        public boolean canApply(AnvilOperation operation, AnvilOperationState state) {
            var itemMeta = state.getBase().getMeta();

            return itemMeta != null
                    && !Objects.equals(itemMeta.displayName(), state.getAnvilInventory().getRenameText());
        }

        @Override
        public AnvilFunctionResult getResult(AnvilOperation operation, AnvilOperationState state) {
            return new AnvilFunctionResult() {
                @Override
                public int getLevelCostIncrease() {
                    return 1;
                }

                @Override
                public void modifyResult(ItemMeta itemMeta) {
                    if (itemMeta == null) {
                        return;
                    }

                    itemMeta.setDisplayName(state.getAnvilInventory().getRenameText());
                    if (itemMeta instanceof Repairable repairable) {
                        int repairCost = Math.max(
                                ItemUtil.getRepairCost(state.getBase().getMeta()),
                                ItemUtil.getRepairCost(state.getBase().getMeta())
                        );
                        repairable.setRepairCost(repairCost);
                    }
                }
            };
        }
    };

    AnvilFunction UPDATE_PRIOR_WORK_COST = new AnvilFunction() {
        @Override
        public boolean canApply(AnvilOperation operation, AnvilOperationState state) {
            return state.getBase().getMeta() instanceof Repairable;
        }

        @Override
        public AnvilFunctionResult getResult(AnvilOperation operation, AnvilOperationState state) {
            return new AnvilFunctionResult() {
                @Override
                public int getLevelCostIncrease() {
                    return 0;
                }

                @Override
                public void modifyResult(ItemMeta itemMeta) {
                    if (itemMeta instanceof Repairable repairable) {
                        int priorRepairCost = Math.max(
                                ItemUtil.getRepairCost(state.getBase().getMeta()),
                                ItemUtil.getRepairCost(state.getBase().getMeta())
                        );
                        repairable.setRepairCost(priorRepairCost * 2 - 1);
                    }
                }
            };
        }
    };

    AnvilFunction REPAIR_WITH_MATERIAL = new AnvilFunction() {
        @Override
        public boolean canApply(AnvilOperation operation, AnvilOperationState state) {
            MetaCachedStack base = state.getBase();
            return operation.canRepairItemBy(base.getItemStack(), state.getAddition().getItemStack())
                    && base.getItemStack().getType().getMaxDurability() > 0
                    && base.getMeta() instanceof Damageable damageable
                    && damageable.getDamage() > 0;
        }

        @Override
        public AnvilFunctionResult getResult(AnvilOperation operation, AnvilOperationState state) {
            if (!(state.getBase().getMeta() instanceof Damageable damageable)) {
                return AnvilFunctionResult.EMPTY;
            }

            int missingDurability = damageable.getDamage();

            if (missingDurability < 1) {
                return AnvilFunctionResult.EMPTY;
            }

            int repairs = 0;

            int damageRepairedPerMaterial = state.getBase().getItemStack().getType().getMaxDurability() / 4;

            while (missingDurability > 0 && repairs < state.getAddition().getItemStack().getAmount()) {
                missingDurability -= damageRepairedPerMaterial;
                ++repairs;
            }

            int totalRepairs = repairs;
            int resultDamage = Math.max(0, missingDurability);

            return new AnvilFunctionResult() {
                @Override
                public int getLevelCostIncrease() {
                    return totalRepairs;
                }

                @Override
                public int getMaterialCostIncrease() {
                    return totalRepairs;
                }

                @Override
                public void modifyResult(ItemMeta itemMeta) {
                    if (itemMeta instanceof Damageable damageable) {
                        damageable.setDamage(resultDamage);
                    }
                }
            };

        }
    };

    AnvilFunction REPAIR_WITH_COMBINATION = new AnvilFunction() {
        @Override
        public boolean canApply(AnvilOperation operation, AnvilOperationState state) {
            Material baseType = state.getBase().getItemStack().getType();
            return baseType == state.getAddition().getItemStack().getType()
                    && baseType.getMaxDurability() > 0
                    && state.getBase().getMeta() instanceof Damageable damageable
                    && damageable.getDamage() > 0;
        }

        @Override
        public AnvilFunctionResult getResult(AnvilOperation operation, AnvilOperationState state) {
            if (!(state.getBase().getMeta() instanceof Damageable baseDamageable
                    && state.getAddition().getMeta() instanceof Damageable additionDamageable)) {
                return AnvilFunctionResult.EMPTY;
            }

            int missingDurability = baseDamageable.getDamage();

            if (missingDurability < 1) {
                return AnvilFunctionResult.EMPTY;
            }

            int maxDurability = state.getBase().getItemStack().getType().getMaxDurability();

            int restoredDurability = maxDurability - additionDamageable.getDamage();

            restoredDurability += maxDurability * .12;

            int resultDamage = Math.max(0, missingDurability - restoredDurability);

            return new AnvilFunctionResult() {
                @Override
                public int getLevelCostIncrease() {
                    return 2;
                }

                @Override
                public void modifyResult(ItemMeta itemMeta) {
                    if (itemMeta instanceof Damageable damageable) {
                        damageable.setDamage(resultDamage);
                    }
                }
            };
        }
    };

    AnvilFunction COMBINE_ENCHANTMENTS_JAVA_EDITION = new CombineEnchantments() {
        @Override
        protected int getTotalCost(int baseCost, int oldLevel, int newLevel) {
            return baseCost * newLevel;
        }

        @Override
        protected int getNonApplicableCost() {
            return 1;
        }
    };



    boolean canApply(AnvilOperation operation, AnvilOperationState state);

    AnvilFunctionResult getResult(AnvilOperation operation, AnvilOperationState state);

}
