package org.shadowz.phpsend.API;

import org.bukkit.plugin.Plugin;
import org.shadowz.phpsend.MainPhpSend;
import org.shadowz.phpsend.Threads.PhpSendListenThread;

/**
 * The Main PHPSendAPI class. This class is used to access the API of PHPSend.
 */
public class PHPSendAPI {

   private static MainPhpSend pl;

   private static PHPSendAPI instance;

   private static PhpSendListenThread t;

   /**
    * Instantiates a new API. <bold>Only</bold> used by the PHPSend plugin.
    * 
    * @param pl the PHPSend plugin instance
    * @param th the PHPSend thread
    */
   public PHPSendAPI(MainPhpSend pl, PhpSendListenThread th) {
      org.shadowz.phpsend.API.PHPSendAPI.pl = pl;
      instance = this;
   }

   /**
    * Gets an instance of the API. If you somehow get instance of this class, I suppose this works. You are supposed to use the registerPlugin() method though.
    * 
    * @param name the name
    * @return the api
    */
   public APIMethods getAPI(String name) {
      return new APIMethods(this, name);
   }

   /**
    * Register your plugin. Used to register your plugin and get a instance of the methods used in the API.
    * 
    * @param p the p
    * @return the aPI methods
    */
   public static APIMethods registerPlugin(Plugin p) {
      pl.getLogger().info("The plugin " + p.getName() + " has been registered!");
      return instance.getAPI(p.getName());
   }

   /**
    * The Class APIMethods.
    */
   public class APIMethods {

      /** The name of the plugin. */
      private String name;

      /**
       * Instantiates a new instance of the APIMethods. 
       * 
       * @param name the name
       */
      public APIMethods(PHPSendAPI api, String name) {
         this.name = name;
      }

      /**
       * Send data.
       * 
       * @param msg the msg
       * @return true, if successful
       */
      public boolean send(String msg) {
         return t.con.Send(name + ":" + msg);
      }

      /**
       * Connect. Currently unused
       * 
       * @param host the host
       * @param password the password
       * @param port the port
       * @return true, if successful
       */
      public boolean connect(String host, String password, String port) {
         return false;
      }

      /**
       * Post data to the address defined in the config.
       * 
       * @param args the arguments to post.
       * @return the response from the server
       */
      public String post(String args[]) {
         return pl.post(args);
      }

      /**
       * Post data to an address. Only use if you are not posting to the address in the config.
       * 
       * @param addr the address to post to.
       * @param args the arguments to post.
       * @return the response from the server
       */
      public String post(String addr, String args[]) {
         return pl.post(addr, args);
      }
   }

}
