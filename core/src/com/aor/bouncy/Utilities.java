package com.aor.bouncy;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * By: kraskevich
 * at http://stackoverflow.com/questions/28151958/performant-way-to-select-n-random-distinct-ints-in-java
 */

public class Utilities {
    private static final Random random = new Random();

    /**
     * Converts a set of Integer to an array of int.
     */
    private static int[] setToArray(Set<Integer> aSet) {
        int[] result = new int[aSet.size()];
        int index = 0;
        for (int number : aSet) {
            result[index] = number;
            index++;
        }
        return result;
    }

    /**
     * Generates an array of min(count, maxValue) distinct random ints
     * from [0, maxValue - 1] range.
     * @param count The number of elements to be generated.
     * @param maxValue The upper bound of the range(exclusively).
     */
    public static int[] getDistinctRandomNumbers(int count, int maxValue) {
        Set<Integer> was = new HashSet<Integer>();
        for (int i = Math.max(0, maxValue - count); i < maxValue; i++) {
            int curr = i == 0 ? 0 : random.nextInt(i);
            if (was.contains(curr))
                curr = i;
            was.add(curr);
        }
        return setToArray(was);
    }
}
