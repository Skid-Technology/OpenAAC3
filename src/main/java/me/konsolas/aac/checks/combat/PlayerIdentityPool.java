package me.konsolas.aac.checks.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.konsolas.aac.AAC;
import me.konsolas.aac.util.RandomPlayerName;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class PlayerIdentityPool {
    private static final PlayerIdentityPool instance;
    private final List identities = new ArrayList();

    private PlayerIdentityPool() {
        int value = 0;
        Server server = Bukkit.getServer();
        for (OfflinePlayer offlinePlayer : server.getOfflinePlayers()) {
            this.identities.add(new PlayerIdentity(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
            if (value++ > 500) break;
        }
    }

    private static UUID parseUndashedUuid(String str) {
        String str2 = str.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
        return UUID.fromString(str2);
    }

    public static PlayerIdentityPool getInstance() {
        return instance;
    }

    public PlayerIdentity getRandomIdentity() {
        if (this.identities.size() > 0) {
            return (PlayerIdentity)this.identities.get(AAC.random.nextInt(this.identities.size()));
        }
        return new PlayerIdentity(UUID.randomUUID(), RandomPlayerName.generateName());
    }

    public PlayerIdentity getDifferentRandomIdentity(UUID uUID) {
        if (this.identities.size() > 0) {
            boolean flag = true;
            for (int i = 0; i < 5; ++i) {
                PlayerIdentity playerIdentity = (PlayerIdentity)this.identities.get(AAC.random.nextInt(this.identities.size()));
                Server server = Bukkit.getServer();
                for (Player player : server.getOnlinePlayers()) {
                    if (!player.getUniqueId().equals(playerIdentity.getUuid())) continue;
                    flag = false;
                }
                if (flag) {
                    return playerIdentity;
                }
                flag = true;
            }
        }
        return new PlayerIdentity(UUID.randomUUID(), RandomPlayerName.generateName());
    }

    public PlayerIdentity getOnlineOrRandomIdentity(UUID uUID) {
        for (int i = 0; i < 5; ++i) {
            Server server = Bukkit.getServer();
            int value = AAC.random.nextInt(server.getOnlinePlayers().size());
            Player player = Bukkit.getPlayer((UUID)uUID);
            int value2 = 0;
            Server server2 = Bukkit.getServer();
            for (Player player2 : server2.getOnlinePlayers()) {
                if (value2 == value) {
                    player = player2;
                }
                ++value2;
            }
            if (player.getUniqueId().equals(uUID)) continue;
            return new PlayerIdentity(player.getUniqueId(), player.getName());
        }
        return null;
    }

    public PlayerIdentity generateRandomIdentity() {
        return new PlayerIdentity(UUID.randomUUID(), RandomPlayerName.generateName());
    }

    static {
        instance = new PlayerIdentityPool();
    }
}

