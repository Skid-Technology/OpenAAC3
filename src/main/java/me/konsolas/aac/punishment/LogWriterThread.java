package me.konsolas.aac.punishment;


public class LogWriterThread
extends Thread {
    final LogManager logManager;

    public LogWriterThread(LogManager logManager, String str) {        super(str);

        this.logManager = logManager;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String str;
                String str2 = str = (String)LogManager.getLogQueue(this.logManager).take();
                LogManager.getPlugin(this.logManager).writeLog(str2);
            }
        }
        catch (InterruptedException interruptedException) {
            LogManager.getPlugin(this.logManager).getLogger().info("Thread AAC-LogWriter exiting");
            return;
        }
    }
}

