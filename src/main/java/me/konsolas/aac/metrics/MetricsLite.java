package me.konsolas.aac.metrics;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MetricsLite {
    private static boolean logFailedRequests;
    private static String serverUUID;
    private final JavaPlugin plugin;

    public MetricsLite(JavaPlugin javaPlugin) {
        if (javaPlugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null!");
        }
        this.plugin = javaPlugin;
        File file = new File(javaPlugin.getDataFolder().getParentFile(), "bStats");
        File file2 = new File(file, "config.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)file2);
        if (!yamlConfiguration.isSet("serverUuid")) {
            yamlConfiguration.addDefault("enabled", (Object)true);
            yamlConfiguration.addDefault("serverUuid", (Object)UUID.randomUUID().toString());
            yamlConfiguration.addDefault("logFailedRequests", (Object)false);
            yamlConfiguration.options().header("bStats collects some data for plugin authors like how many servers are using their plugins.\nTo honor their work, you should not disable it.\nThis has nearly no effect on the server performance!\nCheck out https://bStats.org/ to learn more :)").copyDefaults(true);
            try {
                yamlConfiguration.save(file2);
            }
            catch (IOException iOException) {
            }
        }
        serverUUID = yamlConfiguration.getString("serverUuid");
        logFailedRequests = yamlConfiguration.getBoolean("logFailedRequests", false);
        if (yamlConfiguration.getBoolean("enable", true)) {
            boolean flag = false;
            for (Class<?> clazz : Bukkit.getServicesManager().getKnownServices()) {
                try {
                    clazz.getField("B_STATS_VERSION");
                    flag = true;
                    break;
                }
                catch (NoSuchFieldException noSuchFieldException) {
                }
            }
            Bukkit.getServicesManager().register(MetricsLite.class, this, (Plugin)javaPlugin, ServicePriority.Normal);
            if (!flag) {
                this.startSubmitting();
            }
        }
    }

    private static void sendData(JSONObject jSONObject) throws Exception {
        if (jSONObject == null) {
            throw new IllegalArgumentException("Data cannot be null!");
        }
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalAccessException("This method must not be called from the main thread!");
        }
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection)new URL("https://bStats.org/submitData/bukkit").openConnection();
        byte[] bArray = MetricsLite.compress(jSONObject.toString());
        httpsURLConnection.setRequestMethod("POST");
        httpsURLConnection.addRequestProperty("Accept", "application/json");
        httpsURLConnection.addRequestProperty("Connection", "close");
        httpsURLConnection.addRequestProperty("Content-Encoding", "gzip");
        httpsURLConnection.addRequestProperty("Content-Length", String.valueOf(bArray.length));
        httpsURLConnection.setRequestProperty("Content-Type", "application/json");
        httpsURLConnection.setRequestProperty("User-Agent", "MC-Server/1");
        httpsURLConnection.setDoOutput(true);
        DataOutputStream dataOutputStream = new DataOutputStream(httpsURLConnection.getOutputStream());
        dataOutputStream.write(bArray);
        dataOutputStream.flush();
        dataOutputStream.close();
        httpsURLConnection.getInputStream().close();
    }

    private static byte[] compress(String str) throws IOException {
        if (str == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        gZIPOutputStream.write(str.getBytes("UTF-8"));
        gZIPOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private void startSubmitting() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate((TimerTask)new MetricsSubmitTask(this, timer), 300000L, 1800000L);
    }

    public JSONObject getPluginData() {
        JSONObject jSONObject = new JSONObject();
        String str = this.plugin.getDescription().getName();
        String str2 = this.plugin.getDescription().getVersion();
        jSONObject.put((Object)"pluginName", (Object)str);
        jSONObject.put((Object)"pluginVersion", (Object)str2);
        JSONArray jSONArray = new JSONArray();
        jSONObject.put((Object)"customCharts", (Object)jSONArray);
        return jSONObject;
    }

    private JSONObject getServerData() {
        int i;
        Method method;
        try {
            method = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers", new Class[0]);
            i = method.getReturnType().equals(Collection.class) ? ((Collection)method.invoke(Bukkit.getServer(), new Object[0])).size() : ((Player[])method.invoke(Bukkit.getServer(), new Object[0])).length;
        }
        catch (Exception exception) {
            method = null;
            i = Bukkit.getOnlinePlayers().size();
        }
        int value = Bukkit.getOnlineMode() ? 1 : 0;
        String str = Bukkit.getVersion();
        str = str.substring(str.indexOf("MC: ") + 4, str.length() - 1);
        String str2 = System.getProperty("java.version");
        String str3 = System.getProperty("os.name");
        String str4 = System.getProperty("os.arch");
        String str5 = System.getProperty("os.version");
        int value2 = Runtime.getRuntime().availableProcessors();
        JSONObject jSONObject = new JSONObject();
        jSONObject.put((Object)"serverUUID", (Object)serverUUID);
        jSONObject.put((Object)"playerAmount", (Object)i);
        jSONObject.put((Object)"onlineMode", (Object)value);
        jSONObject.put((Object)"bukkitVersion", (Object)str);
        jSONObject.put((Object)"javaVersion", (Object)str2);
        jSONObject.put((Object)"osName", (Object)str3);
        jSONObject.put((Object)"osArch", (Object)str4);
        jSONObject.put((Object)"osVersion", (Object)str5);
        jSONObject.put((Object)"coreCount", (Object)value2);
        return jSONObject;
    }

    private void submitData() {
        JSONObject jSONObject = this.getServerData();
        JSONArray jSONArray = new JSONArray();
        for (Class<?> clazz : Bukkit.getServicesManager().getKnownServices()) {
            try {
                clazz.getField("B_STATS_VERSION");
                for (RegisteredServiceProvider<?> registeredServiceProvider : Bukkit.getServicesManager().getRegistrations(clazz)) {
                    try {
                        jSONArray.add(registeredServiceProvider.getService().getMethod("getPluginData", new Class[0]).invoke(registeredServiceProvider.getProvider(), new Object[0]));
                    }
                    catch (IllegalAccessException | NoSuchMethodException | NullPointerException | InvocationTargetException exception) {}
                }
            }
            catch (NoSuchFieldException noSuchFieldException) {
            }
        }
        jSONObject.put((Object)"plugins", (Object)jSONArray);
        new Thread(() -> this.sendMetricsAsync(jSONObject)).start();
    }

    private void sendMetricsAsync(JSONObject jSONObject) {
        block2: {
            try {
                MetricsLite.sendData(jSONObject);
            }
            catch (Exception exception) {
                if (!logFailedRequests) break block2;
                this.plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats of " + this.plugin.getName(), exception);
            }
        }
    }

    static JavaPlugin access$000(MetricsLite metricsLite) {
        return metricsLite.plugin;
    }

    static void access$100(MetricsLite metricsLite) {
        metricsLite.submitData();
    }
}

