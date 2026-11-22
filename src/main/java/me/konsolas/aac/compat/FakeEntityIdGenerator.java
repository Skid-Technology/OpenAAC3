package me.konsolas.aac.compat;

import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.World;

public class FakeEntityIdGenerator {
    public static int nextEntityId(World world) {
        return ThreadLocalRandom.current().nextInt(0x3FFFFFFF, Integer.MAX_VALUE);
    }
}

