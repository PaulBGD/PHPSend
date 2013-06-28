package main.java.org.shadowz.phpsend;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class Utils {

   public static String prefix = "&8[&cPHPSend&8]&a ";
   
   public static String color(String msg){
      return color(msg, true);
   }
   public static String color(String msg, boolean pf){
      String pre = "";
      if(pf)
         pre = prefix;
      return ChatColor.translateAlternateColorCodes('&', pre + msg);
   }
   public static void log(String msg){
      log(msg, LogType.Normal);
   }

   public static void log(String msg, LogType type){
      ConsoleCommandSender p = Bukkit.getConsoleSender();
      if(type.equals(LogType.Normal)){
         p.sendMessage(Utils.color("&8 -=-=-=- &aPHPSend: Message &8-=-=-=-", false));
         p.sendMessage(Utils.color(msg));
         p.sendMessage(Utils.color("&8 -=-=- &a End PHPSend Message &8-=-=-", false));
      } else if(type.equals(LogType.Bad)){
         p.sendMessage(Utils.color("&8 -=-=-=- &ePHPSend: Something Bad! &8-=-=-=-", false));
         p.sendMessage(Utils.color("&e" + msg));
         p.sendMessage(Utils.color("&8 -=-=- &e End PHPSend Message &8-=-=-", false));
      } else if(type.equals(LogType.Broken)){
         p.sendMessage(Utils.color("&8 -=-=- &cPHPSend: Something Broke! &8-=-=-", false));
         p.sendMessage(Utils.color("&c" + msg));
         p.sendMessage(Utils.color("&8 -=-=- &c End PHPSend Message &8-=-=-", false));
      } else if(type.equals(LogType.Horrible)){
         p.sendMessage(Utils.color("&8 -=- &4PHPSend: Something Happened! &8-=-", false));
         p.sendMessage(Utils.color("&4" + msg));
         p.sendMessage(Utils.color("&8 -=-=- &4 End PHPSend Message &8-=-=-", false));
      } else if(type.equals(LogType.Error)){
         p.sendMessage(Utils.color("&8 -=-=-=- &4PHPSend: ERROR! &8-=-=-=-", false));
         p.sendMessage(Utils.color("&e" + msg));
         p.sendMessage(Utils.color("&8 -=-=- &e End PHPSend ERROR &8-=-=-", false));
      }
   }
   
   public enum LogType{
      Normal,
      Bad,
      Broken,
      Horrible, 
      Error
   }
}
