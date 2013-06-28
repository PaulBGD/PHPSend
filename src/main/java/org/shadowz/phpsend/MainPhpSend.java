package main.java.org.shadowz.phpsend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MainPhpSend extends JavaPlugin {
   public Logger log = null;
   PhpSendListenThread t = null;

   ConnectorOut out = null;

   List<PhpSendPlugin> plugins;

   //config vars
   public int port;
   public String password;
   int logLevel;
   boolean useWhitelist;
   int maxthreads;

   public void err(String x) {
      if (logLevel >= 1)
         log.info("(ERR) " + x);
   }

   public void info(String x) {
      if (logLevel >= 2)
         log.info("(INF) " + x);
   }

   public void debug(String x) {
      if (logLevel >= 3)
         log.info("(DBG) " + x);
   }

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
      log = this.getLogger();

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

      t = new PhpSendListenThread(this);
      new Thread(t).start();
      info("PHPsend started main thread.");
   }

   public void onDisable() {
      t.shutdown();
      t.con.drop();
      t = null;
      info("PHPsend disabled succesfully!");
   }

   /* Config operation methods by Chlorek*/
   private static FileConfiguration configFile = null;
   private static File file = null;
   private static FileInputStream fwlist = null;

   public List<String> wlist;

   public void reloadConfig() {
      if (file == null) {
         file = new File(getDataFolder(), "config.yml");

         if (!file.exists()) {
            info("No config found! Writing default config.");
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
            info("Config loaded.");
      }
      configFile = YamlConfiguration.loadConfiguration(file);
   }

   public void reloadWhitelist() {
      if (fwlist != null)
         fwlist = null;

      try {
         File f = new File(getDataFolder(), "wlist.txt");

         if (!f.exists()) {
            err("WHITELIST ENABLED IN CONFIG BUT NO FILE FOUND!");
            return;
         }

         fwlist = new FileInputStream(f);
         BufferedReader r = new BufferedReader(new InputStreamReader(fwlist));

         String line;
         while ((line = r.readLine()) != null)
            wlist.add(line);
      } catch (Exception e) {
         err("WHITELIST ENABLED IN CONFIG BUT NO FILE FOUND!");
         return;
      }

      info("Loaded " + wlist.size() + " whitelisted adresses");
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
            sender.sendMessage("Active connections: " + t.threads + " / " + maxthreads);
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
            err("Website command, arg[" + i + "] has no equation (\"=\") character...");
            return "";
         }
         vargs[i] = args[i];
      }
      String postPassword = getConfig().getString("postPassword");

      if (postPassword == null) {
         err("No post password set.");
         return "";
      }
      vargs[args.length] = "PHPSpass=" + sha1(postPassword);
      return t.con.post(addr, vargs);
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
         Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + file, e);
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
