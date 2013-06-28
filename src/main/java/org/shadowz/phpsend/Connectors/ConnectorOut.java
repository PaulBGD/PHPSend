package main.java.org.shadowz.phpsend.Connectors;

import java.io.*;
import java.net.*;

import main.java.org.shadowz.phpsend.MainPhpSend;

public class ConnectorOut {
   public Socket mSocket = null;
   public ServerSocket sSocket = null;
   public BufferedWriter out = null;
   public BufferedReader in = null;
   public int DEFAULTPORT = 11201;

   public int connectPhpSend(String _host, String pass, int port) {
      int r = connect(_host, port);
      if (r != 0)
         return r;

      r = auth(pass);

      if (r != 0)
         return r;

      return 0;
   }

   public int command(String command) {
      Send(command);
      String r = Recv();
      return Integer.parseInt(r);
   }

   public int auth(String pass) {
      String sha1pass = MainPhpSend.sha1(pass);
      if (!Send(sha1pass))
         return 3;
      String r = Recv();

      if (r == null || r.equals("PHPSerror"))
         return 4;

      return 0;
   }

   public int connect(String _host, int port) {
      if (port == 0)
         port = DEFAULTPORT;
      try {
         mSocket = new Socket(_host, port);
         out = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
         in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
      } catch (UnknownHostException e) {
         System.out.println("Don't know about host: " + _host);
         out = null;
         in = null;
         mSocket = null;
         return 1;
      } catch (IOException e) {
         System.out.println("Couldn't get I/O for " + "the connection to: " + _host);
         out = null;
         in = null;
         mSocket = null;
         return 2;
      }
      return 0;
   }

   public boolean Send(String data) {
      try {
         out.write(data + "\n");
         out.flush();
      } catch (Exception e) {
         return false;
      }
      return true;
   }

   public String Recv() {
      try {
         return in.readLine();
      } catch (Exception e) {
         return "PHPSerror";
      }
   }

   boolean gotData() {
      try {
         return (mSocket.getInputStream().available() != 0);
      } catch (IOException e) {
         return false;
      }
   }
}