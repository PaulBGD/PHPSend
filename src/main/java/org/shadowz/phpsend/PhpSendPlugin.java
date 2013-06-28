package main.java.org.shadowz.phpsend;

public abstract class PhpSendPlugin {
   public MainPhpSend up;
   String name;

   public abstract boolean onWebCommand(String cmd);

   public abstract boolean onWebCommandAsPlayer(String player, String r);

   public boolean send(String msg) {
      return up.listenThread.con.Send(name + ":" + msg);
   }

   public boolean connect(String host, String password, String port) {
      return false;
   }

   public String post(String args[]) {
      return up.post(args);
   }

   public String post(String addr, String args[]) {
      return up.post(addr, args);
   }
}
