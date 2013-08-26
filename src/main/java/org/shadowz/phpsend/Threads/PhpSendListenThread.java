package org.shadowz.phpsend.Threads;

import java.util.logging.Logger;

import org.shadowz.phpsend.MainPhpSend;
import org.shadowz.phpsend.Connectors.Connector;

public class PhpSendListenThread implements Runnable {

   public Connector con;

   MainPhpSend up;
   Logger log;

   public int threads;

   boolean run = true;

   public void err(String x) {
      if (up.logLevel >= 1)
         log.info("(ERR) " + x);
   }

   public void info(String x) {
      if (up.logLevel >= 2)
         log.info("(INF) " + x);
   }

   public void debug(String x) {
      if (up.logLevel >= 3)
         log.info("(DBG) " + x);
   }

   public PhpSendListenThread(MainPhpSend _up) {
      up = _up;
      con = new Connector(this);
      log = up.getLogger();
      threads = 0;
   }

   public void shutdown() {
      run = false;
   }

   public int prepareConnection() {
      if (!con.hosting())
         con.host(up.port);

      info("Started hosting.");
      int r = -1;

      r = con.accept();

      if (r == 1)
         return 3;

      if (r != 0 || !this.run) {
         con.drop();
         return 1;
      }
      info("Client connected!");

      if (threads >= up.maxthreads) {
         con.Send("PHPSbusy");
         con.drop();
         return 5;
      }

      if (up.useWhitelist) {
         debug("Whitelist check.");

         String addr = con.mSocket.getRemoteSocketAddress().toString();

         addr = addr.substring(1, addr.indexOf(":"));

         boolean is = false;
         for (int i = 0; i < up.wlist.size(); i++)
            if (addr.equals(up.wlist.get(i)))
               is = true;
         if (!is) {
            info("Client not whitelisted: " + addr);
            con.Send("PHPSpass2");
            con.drop();
            return 4;
         }
      }

      String sha1pass = con.Recv();

      if (sha1pass == null || sha1pass.equals("PHPSerror")) {
         err("PHP client disconnected without ending message (pass)");
         con.drop();
         return 1;
      }

      if (!sha1pass.equals(MainPhpSend.sha1(up.password))) {
         err("Bad password! Cleaning up...");
         con.Send("PHPSpass1");
         con.drop();
         return 2;
      }
      con.Send("PHPSpass0");
      return 0;
   }

   @Override
   public void run() {
      while (run) {
         int s = prepareConnection();

         if (s != 0)
            continue;

         threads++;

         new Thread(new PhpSendClientHandlerThread(con.passConnection())).start();
      }
      con.drop();
   }
}
