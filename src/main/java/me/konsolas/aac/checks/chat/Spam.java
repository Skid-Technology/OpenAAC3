package me.konsolas.aac.checks.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import me.konsolas.aac.AAC;
import me.konsolas.aac.checks.Detector;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import me.konsolas.aac.api.HackType;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public final class Spam
extends Detector {
    private final ConcurrentMap messageCounts;
    private final ExpiringMap lastMessages;
    private final ExpiringMap flaggedMessages;
    private final ExpiringMap recentSpammers;
    private final ExpiringMap chatCooldowns;
    private volatile List<String> whitelistedMessages;
    private int messageLimit;
    private int chatSpeedTicks;

    public Spam(AAC aAC) {        super(aAC);

        this.messageCounts = new ConcurrentHashMap();
        ArrayList arrayList = new ArrayList();
        Spam spam = this;
        spam.whitelistedMessages = arrayList;
        int value = 5;
        Spam spam2 = this;
        spam2.messageLimit = value;
        int value2 = 10;
        Spam spam3 = this;
        spam3.chatSpeedTicks = value2;
        Spam spam4 = this;
        int value3 = spam4.plugin.getConfig().getInt("spam.message_limit");
        Spam spam5 = this;
        spam5.messageLimit = value3;
        Spam spam6 = this;
        List list = spam6.plugin.getConfig().getStringList("spam.whitelist");
        Spam spam7 = this;
        spam7.whitelistedMessages = list;
        Spam spam8 = this;
        int value4 = spam8.plugin.getConfig().getInt("spam.chat_speed");
        Spam spam9 = this;
        spam9.chatSpeedTicks = value4;
        Server server = Bukkit.getServer();
        Spam spam10 = this;
        server.getScheduler().scheduleSyncRepeatingTask((Plugin)spam10.plugin, this::resetChatCounts, 200L, 200L);
        Server server2 = Bukkit.getServer();
        Spam spam11 = this;
        server2.getScheduler().scheduleSyncRepeatingTask((Plugin)spam11.plugin, this::decaySpamViolations, 200L, 300L);
        ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        long ticks = 10L;
        this.lastMessages = ExpiringMap.builder().expirationPolicy(expirationPolicy).expiration(ticks, timeUnit).build();
        ExpirationPolicy expirationPolicy2 = ExpirationPolicy.CREATED;
        TimeUnit timeUnit2 = TimeUnit.SECONDS;
        long ticks2 = 5L;
        this.flaggedMessages = ExpiringMap.builder().expirationPolicy(expirationPolicy2).expiration(ticks2, timeUnit2).build();
        ExpirationPolicy expirationPolicy3 = ExpirationPolicy.CREATED;
        TimeUnit timeUnit3 = TimeUnit.SECONDS;
        long ticks3 = 10L;
        this.recentSpammers = ExpiringMap.builder().expirationPolicy(expirationPolicy3).expiration(ticks3, timeUnit3).build();
        ExpirationPolicy expirationPolicy4 = ExpirationPolicy.CREATED;
        Spam spam12 = this;
        TimeUnit timeUnit4 = TimeUnit.MILLISECONDS;
        long ticks4 = spam12.chatSpeedTicks * 50;
        this.chatCooldowns = ExpiringMap.builder().expirationPolicy(expirationPolicy4).expiration(ticks4, timeUnit4).build();
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        long ticks = 113218805911140L;
        Spam spam = this;
        AAC aAC = spam.plugin;
        Player player = asyncPlayerChatEvent.getPlayer();
        if (aAC.checkManager.isPlayerExempt(player)) {
            return;
        }
        Player player2 = asyncPlayerChatEvent.getPlayer();
        Spam spam2 = this;
        if (spam2.chatCooldowns.containsKey(player2.getUniqueId())) {
            if (this.recordViolation(player2.getUniqueId(), 5, HackType.SPAM, player2.getName() + " failed Spam: tried to chat too quickly", ticks)) {
                asyncPlayerChatEvent.setCancelled(true);
                Spam spam3 = this;
                AAC aAC2 = spam3.plugin;
                player2.sendMessage(AAC.messagePrefix + aAC2.messages.getMessage("spam_too_fast"));
            }
            return;
        }
        Spam spam4 = this;
        if (spam4.messageCounts.containsKey(player2.getUniqueId())) {
            Spam spam5 = this;
            Spam spam6 = this;
            spam5.messageCounts.put(player2.getUniqueId(), (Integer)spam6.messageCounts.get(player2.getUniqueId()) + 1);
            Spam spam7 = this;
            Spam spam8 = this;
            if ((Integer)spam7.messageCounts.get(player2.getUniqueId()) == spam8.messageLimit) {
                Spam spam9 = this;
                if (spam9.recentSpammers.containsKey(player2.getUniqueId())) {
                    if (this.recordViolation(player2.getUniqueId(), 6, HackType.SPAM, player2.getName() + " failed Spam: sent too many messages", ticks)) {
                        Spam spam10 = this;
                        AAC aAC3 = spam10.plugin;
                        player2.sendMessage(AAC.messagePrefix + aAC3.messages.getMessage("spam_slow_chat"));
                    }
                } else if (this.recordViolation(player2.getUniqueId(), 4, HackType.SPAM, player2.getName() + " failed Spam: sent too many messages", ticks)) {
                    Spam spam11 = this;
                    spam11.recentSpammers.put(player2.getUniqueId(), (Object)true);
                    Spam spam12 = this;
                    AAC aAC4 = spam12.plugin;
                    player2.sendMessage(AAC.messagePrefix + aAC4.messages.getMessage("spam_slow_chat"));
                }
                return;
            }
        }
        String str = asyncPlayerChatEvent.getMessage();
        Spam spam13 = this;
        for (String str2 : spam13.whitelistedMessages) {
            String str3;
            String str4 = str2;
            if (!ChatSimilarity.isSimilar(str4, str3 = str)) continue;
            return;
        }
        Spam spam14 = this;
        if (spam14.lastMessages.containsKey(player2.getUniqueId())) {
            Spam spam15 = this;
            String str5 = str;
            String str6 = (String)spam15.lastMessages.get(player2.getUniqueId());
            if (ChatSimilarity.isSimilar(str5, str6)) {
                Spam spam16 = this;
                if (spam16.recentSpammers.containsKey(player2.getUniqueId())) {
                    if (this.recordViolation(player2.getUniqueId(), 4, HackType.SPAM, player2.getName() + " failed Spam: repeated last message", ticks)) {
                        Spam spam17 = this;
                        AAC aAC5 = spam17.plugin;
                        player2.sendMessage(AAC.messagePrefix + aAC5.messages.getMessage("spam_repeat"));
                    }
                } else if (this.recordViolation(player2.getUniqueId(), 2, HackType.SPAM, player2.getName() + " failed Spam: repeated last message", ticks)) {
                    Spam spam18 = this;
                    spam18.recentSpammers.put(player2.getUniqueId(), (Object)true);
                    Spam spam19 = this;
                    AAC aAC6 = spam19.plugin;
                    player2.sendMessage(AAC.messagePrefix + aAC6.messages.getMessage("spam_repeat"));
                }
                return;
            }
        }
        Spam spam20 = this;
        Iterator iterator = spam20.flaggedMessages.keySet().iterator();
        String str7;
        while (iterator.hasNext()) {
            String str8 = str;
            str7 = (String)iterator.next();
            String str9 = str7;
            if (!ChatSimilarity.isSimilar(str8, str9)) continue;
            Spam spam21 = this;
            spam21.flaggedMessages.put(str, (Object)true);
            Spam spam22 = this;
            if (spam22.recentSpammers.containsKey(player2.getUniqueId())) {
                if (this.recordViolation(player2.getUniqueId(), 3, HackType.SPAM, player2.getName() + " failed Spam: tried to repeat a suspicious message (" + str + ")", ticks)) {
                    Spam spam23 = this;
                    AAC aAC7 = spam23.plugin;
                    player2.sendMessage(AAC.messagePrefix + aAC7.messages.getMessage("spam_repeat_other"));
                    asyncPlayerChatEvent.setCancelled(true);
                }
            } else if (this.recordViolation(player2.getUniqueId(), 2, HackType.SPAM, player2.getName() + " failed Spam: tried to repeat a suspicious message (" + str + ")", ticks)) {
                Spam spam24 = this;
                AAC aAC8 = spam24.plugin;
                player2.sendMessage(AAC.messagePrefix + aAC8.messages.getMessage("spam_repeat_other"));
                asyncPlayerChatEvent.setCancelled(true);
                Spam spam25 = this;
                spam25.recentSpammers.put(player2.getUniqueId(), (Object)true);
            }
            return;
        }
        Spam spam26 = this;
        iterator = spam26.lastMessages.values().iterator();
        while (iterator.hasNext()) {
            String str10 = str;
            str7 = (String)iterator.next();
            String str11 = str7;
            if (!ChatSimilarity.isSimilar(str10, str11)) continue;
            Spam spam27 = this;
            spam27.flaggedMessages.put(str, (Object)true);
        }
        Spam spam28 = this;
        spam28.lastMessages.put(player2.getUniqueId(), str);
        Spam spam29 = this;
        spam29.chatCooldowns.put(player2.getUniqueId(), (Object)true);
    }

    private void decaySpamViolations() {
        Server server = Bukkit.getServer();
        Iterator iterator = server.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            this.decreaseViolationLevel(player.getUniqueId(), 1);
        }
    }

    private void resetChatCounts() {
        Server server = Bukkit.getServer();
        Iterator iterator = server.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            Spam spam = this;
            if (!spam.messageCounts.containsKey(player.getUniqueId())) {
                Spam spam2 = this;
                spam2.messageCounts.put(player.getUniqueId(), 0);
            }
            Spam spam3 = this;
            spam3.messageCounts.put(player.getUniqueId(), 0);
        }
    }
}

