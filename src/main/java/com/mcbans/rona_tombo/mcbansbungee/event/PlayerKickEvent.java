package com.mcbans.rona_tombo.mcbansbungee.event;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class PlayerKickEvent extends Event implements Cancellable{
	private boolean isCancelled = false;

	private String player;
	private String sender;
	private String reason, playerUUID, senderUUID;

	public PlayerKickEvent(String player, String playerUUID, String sender, String senderUUID, String reason){
		this.player = player;
		this.sender = sender;
		this.reason = reason;
		this.playerUUID = playerUUID;
		this.senderUUID = senderUUID;
	}

	public UUID getPlayerUUID(){
		return UUID.fromString(playerUUID);
	}

	public UUID getSenderUUID(){
		return UUID.fromString(senderUUID);
	}

	public String getPlayer(){
		return player;
	}

	public String getSender(){
		return sender;
	}

	public String getReason(){
		return reason;
	}

	public void setReason(String reason){
		this.reason = reason;
	}

	@Override
	public boolean isCancelled(){
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancelled){
		isCancelled = cancelled;
	}
}
