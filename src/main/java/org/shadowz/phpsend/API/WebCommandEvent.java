package org.shadowz.phpsend.API;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WebCommandEvent extends Event implements Cancellable {

   private String cmd;

   public WebCommandEvent(String cmd) {
      this.cmd = cmd;
   }

   private static final HandlerList handlers = new HandlerList();

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public String getCommand() {
      return cmd;
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
