package org.shadowz.phpsend.API;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The Bukkit Event WebCommandEvent.
 */
public class WebCommandEvent extends Event implements Cancellable {

   /** The command. */
   private String cmd;

   /**
    * Instantiates a new web command event.
    *
    * @param cmd the cmd
    */
   public WebCommandEvent(String cmd) {
      this.cmd = cmd;
   }

   /** The Constant handlers. */
   private static final HandlerList handlers = new HandlerList();

   /* (non-Javadoc)
    * @see org.bukkit.event.Event#getHandlers()
    */
   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   /**
    * Gets the command used.
    *
    * @return the command
    */
   public String getCommand() {
      return cmd;
   }

   /** If cancelled. */
   private boolean cancelled = false;

   /* (non-Javadoc)
    * @see org.bukkit.event.Cancellable#isCancelled()
    */
   @Override
   public boolean isCancelled() {
      return cancelled;
   }

   /* (non-Javadoc)
    * @see org.bukkit.event.Cancellable#setCancelled(boolean)
    */
   @Override
   public void setCancelled(boolean cancel) {
      cancelled = cancel;
   }

}
