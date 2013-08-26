package org.shadowz.phpsend.Connectors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.shadowz.phpsend.Threads.PhpSendListenThread;

public class Connector {
   ServerSocket sSocket;
   public Socket mSocket = null;
   public BufferedWriter out = null;
   public BufferedReader in = null;
   public int DEFAULTPORT = 11223;
   public PhpSendListenThread up = null;

   public Connector(PhpSendListenThread _up) {
      up = _up;
   }

   public Connector passConnection() {
      Connector c = new Connector(up);
      c.mSocket = mSocket;
      c.in = in;
      c.out = out;

      return c;
   }

   public boolean hosting() {
      if (sSocket == null)
         return false;

      return true;
   }

   public boolean host(int port) {
      up.debug("host.");
      if (port == 0)
         port = DEFAULTPORT;
      try {
         sSocket = new ServerSocket(port);
      } catch (IOException e) {
         up.err("Could not listen on port: " + port);
         return false;
      }
      return true;
   }

   public void drop() {
      try {
         if (mSocket != null)
            mSocket.close();
         if (sSocket != null)
            sSocket.close();
      } catch (IOException e) {
         //e.printStackTrace();
      }

   }

   public int accept() {
      try {
         mSocket = null;
         mSocket = sSocket.accept();

         out = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
         in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
      } catch (Exception e) {
         up.info("Accept Interrupted.");
         return 1;
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

   public String post(String addr, String args[]) {
      try {
         String data = "";
         if (args != null && args.length > 0) {
            data = URLEncoder.encode(args[0], "UTF-8");
            for (int i = 1; i < args.length; i++)
               data += "&" + URLEncoder.encode(args[i], "UTF-8");
         }

         data = data.replace("%3D", "=");
         data = data.replace("%5C=", "%3D");

         URL url = null;

         try {
            url = new URL(addr);
         } catch (Exception ex) {
            up.err("Error with post URL");
            return "";
         }
         URLConnection conn = url.openConnection();
         conn.setDoOutput(true);
         OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
         wr.write(data);
         wr.flush();

         BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         String line;
         String resp = "";
         while ((line = rd.readLine()) != null)
            resp += line + "\n";

         wr.close();
         rd.close();
         return resp;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }
}