package org.shadowz.phpsend.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The Bukkit Event WebPlayerCommandEvent.
 */
public class WebPlayerCommandEvent extends Event implements Cancellable {

   /** The command used. */
   private String cmd;
   
   /** The player. */
   private Player p;

   /**
    * Instantiates a new web player command event.
    *
    * @param p the player
    * @param cmd the command
    */
   public WebPlayerCommandEvent(Player p, String cmd) {
      this.cmd = cmd;
      this.p = p;
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
    * Gets the command.
    *
    * @return the command
    */
   public String getCommand() {
      return cmd;
   }

   /**
    * Gets the player.
    *
    * @return the player
    */
   public Player getPlayer() {
      return p;
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
