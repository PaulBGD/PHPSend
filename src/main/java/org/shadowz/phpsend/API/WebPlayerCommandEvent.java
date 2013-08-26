package org.shadowz.phpsend.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WebPlayerCommandEvent extends Event implements Cancellable {

   private String cmd;
   private Player p;

   public WebPlayerCommandEvent(Player p, String cmd) {
      this.cmd = cmd;
      this.p = p;
   }

   private static final HandlerList handlers = new HandlerList();

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public String getCommand() {
      return cmd;
   }

   public Player getPlayer() {
      return p;
   }

   private boolean cancelled = false;

   @Override
   public boolean isCancelled() {
      return cancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      cancelled = cancel;
   }

}
