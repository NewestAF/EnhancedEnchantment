package com.newestaf.enhancedenchantment.enchant;

public enum EnchantRarity {

    COMMON(10, 1),
    UNCOMMON(5, 2),
    RARE(2, 4),
    VERY_RARE(1, 8),
    UNKNOWN(0, 40);

    private final int weight;
    private final int anvilMultiplier;

    EnchantRarity(int weight, int anvilMultiplier) {
        this.weight = weight;
        this.anvilMultiplier = anvilMultiplier;
    }

    public int getWeight() {
        return weight;
    }

    public int getAnvilValue() {
        return anvilMultiplier;
    }


    static EnchantRarity of(int weight) {
        for (EnchantRarity value : values()) {
            if (value.weight == weight) {
                return value;
            }
        }
        return UNKNOWN;
    }

}

