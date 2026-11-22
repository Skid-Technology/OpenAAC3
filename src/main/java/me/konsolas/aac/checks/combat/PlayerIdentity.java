package me.konsolas.aac.checks.combat;

import java.util.UUID;

public class PlayerIdentity {
    private final UUID uuid;
    private final String name;

    public PlayerIdentity(UUID uUID, String str) {
        this.uuid = uUID;
        this.name = str;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }
}

