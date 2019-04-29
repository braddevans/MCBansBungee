package com.mcbans.rona_tombo.mcbansbungee.event;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class PlayerTempBanEvent extends Event implements Cancellable{
	private boolean isCancelled = false;

	private String player;
	private String playerIP;
	private String sender;
	private String reason;
	private String duration;
	private String measure, playerUUID, senderUUID;

	public PlayerTempBanEvent(String player, String playerUUID, String playerIP, String sender, String senderUUID, String reason, String duration, String measure){
		this.player = player;
		this.playerIP = playerIP;
		this.sender = sender;
		this.reason = reason;
		this.duration = duration;
		this.measure = measure;
		this.playerUUID = playerUUID;
		this.senderUUID = senderUUID;
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

	public String getDuration(){
		return duration;
	}

	public void setDuration(String duration){
		this.duration = duration;
	}

	public String getMeasure(){
		return measure;
	}

	public void setMeasure(String measure){
		this.measure = measure;
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
