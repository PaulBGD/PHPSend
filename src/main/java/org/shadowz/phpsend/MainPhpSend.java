package main.java.org.shadowz.phpsend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import main.java.org.shadowz.phpsend.Connectors.ConnectorOut;
import main.java.org.shadowz.phpsend.Threads.PhpSendListenThread;
import main.java.org.shadowz.phpsend.Utils;
import main.java.org.shadowz.phpsend.Utils.LogType;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MainPhpSend extends JavaPlugin {
   PhpSendListenThread listenThread = null;

   ConnectorOut out = null;

   public List<PhpSendPlugin> plugins;

   //config vars
   public int port;
   public String password;
   public int logLevel;
   public boolean useWhitelist;
   public int maxthreads;

   public static boolean registerPlugin(PhpSendPlugin phpsendapi, String ID) {
      MainPhpSend phpsend = (MainPhpSend) Bukkit.getPluginManager().getPlugin("PHPsend");
      if (phpsend == null)
         return false;
      phpsendapi.up = phpsend;
      phpsendapi.name = ID;
      phpsend.plugins.add((PhpSendPlugin) phpsendapi);
      return true;
   }

   public void onEnable() {
      plugins = new ArrayList<PhpSendPlugin>();
      wlist = new ArrayList<String>();

      reloadConfig();
      port = getConfig().getInt("port", 11223);
      password = getConfig().getString("password", "ERR");
      useWhitelist = getConfig().getBoolean("useWhitelist", true);
      logLevel = getConfig().getInt("logLevel", 2);
      maxthreads = getConfig().getInt("maxThreads", 10);
      reloadWhitelist();

      out = new ConnectorOut();

      listenThread = new PhpSendListenThread(this);
      new Thread(listenThread).start();
      Utils.log("PHPsend started main thread.");
   }

   public void onDisable() {
      listenThread.shutdown();
      listenThread.con.drop();
      listenThread = null;
      Utils.log("PHPsend disabled succesfully!");
   }

   // Config operation methods by Chlorek
   private static FileConfiguration configFile = null;
   private static File file = null;
   private static FileInputStream fwlist = null;

   public List<String> wlist;

   public void reloadConfig() {
      if (file == null) {
         file = new File(getDataFolder(), "config.yml");

         if (!file.exists()) {
            Utils.log("No config found! Writing default config.", LogType.Bad);
            String pass = Long.toHexString(Double.doubleToLongBits(Math.random())); //random password

            getConfig().set("password", pass);
            getConfig().set("postPassword", pass);
            getConfig().set("postDataUrl", "http://localhost/post.php");
            getConfig().set("port", 11223);
            getConfig().set("logLevel", 2);
            getConfig().set("useWhitelist", false);
            getConfig().set("postPrint", true);
            getConfig().set("maxThreads", 10);

            saveConfig();
         } else
            Utils.log("Config loaded.");
      }
      configFile = YamlConfiguration.loadConfiguration(file);
   }

   public void reloadWhitelist() {
      if (useWhitelist) {
         if (fwlist != null)
            fwlist = null;
         try {
            File f = new File(getDataFolder(), "wlist.txt");

            if (!f.exists()) {
               Utils.log("WHITELIST ENABLED IN CONFIG BUT NO FILE FOUND!", LogType.Broken);
               return;
            }

            fwlist = new FileInputStream(f);
            BufferedReader r = new BufferedReader(new InputStreamReader(fwlist));
            String line;

            while ((line = r.readLine()) != null)
               wlist.add(line);
         } catch (Exception e) {
            Utils.log("WHITELIST ENABLED IN CONFIG BUT NO FILE FOUND!\n" + e.getStackTrace(), LogType.Error);
            return;
         }

         Utils.log("Loaded " + wlist.size() + " whitelisted adresses");
      }
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (command.getName().equalsIgnoreCase("website")) {
         String ret = post(args);
         if (getConfig().getBoolean("postPrint", true)) {
            System.out.println("PHPsend page start ------------------");
            int i = 0;
            while (true) {
               int j = ret.indexOf("\\n", i);
               if (j == -1)
                  break;
               System.out.println(ret.substring(i, j));
               i = j + 2;
            }
            System.out.println("PHPsend page end   ------------------");
         }
         return true;
      } else if (command.getName().equalsIgnoreCase("phpsend")) {
         if (args.length < 1)
            return false;
         if (args[0].equals("reload") && args.length == 1) {
            wlist = new ArrayList<String>();

            reloadConfig();
            port = getConfig().getInt("port", 11223);
            password = getConfig().getString("password", "ERR");
            useWhitelist = getConfig().getBoolean("useWhitelist", true);
            logLevel = getConfig().getInt("logLevel", 2);
            maxthreads = getConfig().getInt("maxThreads", 10);
            reloadWhitelist();

            sender.sendMessage("reloaded");
            return true;
         }
         if (args[0].equals("connections") && args.length == 1) {
            sender.sendMessage("Active connections: " + listenThread.threads + " / " + maxthreads);
            return true;
         } else
            return false;
      }
      return false;
   }

   public String post(String addr, String args[]) {
      String vargs[] = new String[args.length + 1];

      for (int i = 0; i < args.length; i++) {
         if (args[i].indexOf('=') == -1) {
            Utils.log("Website command, arg[" + i + "] has no equation (\"=\") character...", LogType.Bad);
            return "";
         }
         vargs[i] = args[i];
      }
      String postPassword = getConfig().getString("postPassword");

      if (postPassword == null) {
         Utils.log("No post password set.", LogType.Bad);
         return "";
      }
      vargs[args.length] = "PHPSpass=" + sha1(postPassword);
      return listenThread.con.post(addr, vargs);
   }

   public String post(String args[]) {
      return post(getConfig().getString("postDataUrl"), args);
   }

   public FileConfiguration getConfig() {
      if (configFile == null) {
         reloadConfig();
      }
      return configFile;
   }

   public void saveConfig() {
      if (configFile == null || file == null) {
         return;
      }
      try {
         configFile.save(file);
      } catch (IOException e) {
         Utils.log("Could not save config to " + file, LogType.Broken);
      }
   }

   public static String sha1(String sha1) {
      try {
         java.security.MessageDigest md = java.security.MessageDigest.getInstance("sha1");
         byte[] array = md.digest(sha1.getBytes());
         StringBuffer sb = new StringBuffer();
         for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
         }
         return sb.toString();
      } catch (Exception e) {
         System.err.println("Error with SHA1...");
      }
      return null;
   }
}
