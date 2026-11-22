package me.konsolas.aac.punishment;

import me.konsolas.aac.util.PlayerDebugMessage;

public class DebugWriterThread
extends Thread {
    final LogManager logManager;

    public DebugWriterThread(LogManager logManager, String str) {
        super(str);
        this.logManager = logManager;
    }

    @Override
    public void run() {
        long ticks = 39994536334614L;
        try {
            while (true) {
                PlayerDebugMessage playerDebugMessage = (PlayerDebugMessage)LogManager.getDebugQueue(this.logManager).take();
                LogManager.appendDebugMessage(this.logManager, playerDebugMessage, ticks);
            }
        }
        catch (InterruptedException interruptedException) {
            LogManager.getPlugin(this.logManager).getLogger().info("Thread AAC-LogWriter exiting");
            return;
        }
    }
}

