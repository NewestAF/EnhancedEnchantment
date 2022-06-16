package com.newestaf.enhancedenchantment.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.Random;
public final class WeightedRandom {

    private WeightedRandom() {}

    public static <T extends Choice> T choose(Random random, Collection<T> choices) {
        int choiceMax = sum(choices);

        if (choiceMax <= 0) {
            throw new IllegalArgumentException("Must provide at least 1 choice with weight!");
        }

        int chosen = random.nextInt(choiceMax);

        for (T choice : choices) {
            chosen -= choice.getWeight();
            if (chosen <= 0) {
                return choice;
            }
        }

        throw new IllegalStateException(
                "Generated an index out of bounds with " + random.getClass().getName());
    }

    private static int sum(Collection<? extends Choice> choices) {
        return choices.stream().mapToInt(Choice::getWeight).sum();
    }

    public interface Choice {
        int getWeight();
    }
}
