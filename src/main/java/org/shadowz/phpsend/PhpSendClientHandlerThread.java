package main.java.org.shadowz.phpsend;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PhpSendClientHandlerThread implements Runnable {

   Connector con;

   public PhpSendClientHandlerThread(Connector _con) {
      con = _con;
   }

   @Override
   public void run() {
      while (true) {
         String player = con.Recv();
         String r = con.Recv();

         Player p = null;

         if (player == null) {
            con.up.err("EndOfStream error");
            con.drop();
         }

         if (!player.equals("[server]")) {
            p = Bukkit.getServer().getPlayer(player);
            if (p == null)
               con.Send("PHPScmd1");
            else
               con.Send("PHSPcmd0");
         } else
            con.Send("PHPScmd0");

         if (r == null || r.equals("PHPSerror")) {
            con.up.err("PHP client disconnected without ending message (cmd)");
            con.drop();
            break;
         }

         if (r.equals("PHPSdisconnect")) {
            con.up.info("PHP client disconnected: " + r);
            con.Send("PHPSdisconnect0");
            con.drop();
            break;
         }

         boolean passFurther = true;
         for (PhpSendPlugin a : con.up.up.plugins) {
            if (player.equals("[server]"))
               passFurther &= a.onWebCommand(r);
            else
               passFurther &= a.onWebCommandAsPlayer(player, r);
         }

         con.up.info("PHP command: " + r);

         if (passFurther) {
            if (player.equals("[server]"))
               Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), r);
            else {
               if (p == null) {
                  con.up.err("No player named " + player + " found...");
                  continue;
               }
               p.performCommand(r);
            }
         }
      }
      con.up.threads--;
   }

}
