package org.shadowz.phpsend.API;

import org.bukkit.plugin.Plugin;
import org.shadowz.phpsend.MainPhpSend;
import org.shadowz.phpsend.Threads.PhpSendListenThread;

public class PHPSendAPI {

   private static MainPhpSend pl;
   private static PHPSendAPI instance;
   private static PhpSendListenThread t;

   public PHPSendAPI(MainPhpSend pl, PhpSendListenThread th) {
      org.shadowz.phpsend.API.PHPSendAPI.pl = pl;
      instance = this;
   }

   public APIMethods getAPI(String name) {
      return new APIMethods(name);
   }

   public static APIMethods registerPlugin(Plugin p) {
      pl.getLogger().info("The plugin " + p.getName() + " has been registered!");
      return instance.getAPI(p.getName());
   }

   public class APIMethods {
      private String name;

      public APIMethods(String name) {
         this.name = name;
      }

      public boolean send(String msg) {
         return t.con.Send(name + ":" + msg);
      }

      public boolean connect(String host, String password, String port) {
         return false;
      }

      public String post(String args[]) {
         return pl.post(args);
      }

      public String post(String addr, String args[]) {
         return pl.post(addr, args);
      }
   }

}
