package com.mcbans.rona_tombo.mcbansbungee.event;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class PlayerGlobalBanEvent extends Event implements Cancellable{
	private boolean isCancelled = false;

	private String player;
	private String playerIP;
	private String sender;
	private String reason, playerUUID, senderUUID;

	public PlayerGlobalBanEvent(String player, String playerUUID, String playerIP, String sender, String senderUUID, String reason){
		this.player = player;
		this.playerIP = playerIP;
		this.sender = sender;
		this.playerUUID = playerUUID;
		this.senderUUID = senderUUID;
		this.reason = reason;
	}

	public UUID getPlayerUUID(){
		return UUID.fromString(playerUUID);
	}

	public UUID getSenderUUID(){
		return UUID.fromString(senderUUID);
	}

	public String getPlayerName(){
		return player;
	}

	public String getPlayerIP(){
		return playerIP;
	}

	public String getSenderName(){
		return sender;
	}

	public void setSenderName(String senderName){
		this.sender = senderName;
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
