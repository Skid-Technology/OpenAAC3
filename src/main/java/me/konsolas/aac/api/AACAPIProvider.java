package me.konsolas.aac.api;

import me.konsolas.aac.AAC;
import me.konsolas.aac.api.AACAPI;

public final class AACAPIProvider {
    private AACAPIProvider() {
        throw new AssertionError();
    }

    public static AACAPI getAPI() {
        return AAC.api;
    }

    public static boolean isAPILoaded() {
        return AAC.api != null;
    }
}

