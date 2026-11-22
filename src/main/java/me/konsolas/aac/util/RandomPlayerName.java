package me.konsolas.aac.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomPlayerName {
    private static final List alphabet = Collections.unmodifiableList(Arrays.asList(AlphabetLetter.values()));
    private static final Random random = new Random();

    public static String generateName() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 2 + random.nextInt(9); i > 0; --i) {
            stringBuilder.append(RandomPlayerName.randomLetter());
        }
        stringBuilder.append(random.nextInt(999));
        return stringBuilder.toString();
    }

    private static String randomLetter() {
        return ((AlphabetLetter)((Object)alphabet.get(random.nextInt(alphabet.size())))).name();
    }
}

