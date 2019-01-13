package com.mcbans.rona_tombo.mcbansbungee.event;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class PlayerBanEvent extends Event implements Cancellable{
	private boolean isCancelled = false;
	private int action_id;
	private String duration, measure, reason, sender, playerIP, player, playerUUID, senderUUID;

	public PlayerBanEvent(String player, String playerUUID, String playerIP, String sender, String senderUUID, String reason, int action_id, String duration, String measure){
		this.player = player;
		this.playerIP = playerIP;
		this.sender = sender;
		this.reason = reason;
		this.action_id = action_id;
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

	public void setSenderBane(String senderName){
		this.sender = senderName;
	}

	public String getReason(){
		return reason;
	}

	public void setReason(String reason){
		this.reason = reason;
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

	public boolean isGlobalBan(){
		return action_id == 0;
	}

	public void setGlobalBan(){
		action_id = 0;
	}

	public boolean isLocalBan(){
		return action_id == 1;
	}

	public void setLocalBan(){
		action_id = 1;
	}

	public boolean isTempBan(){
		return action_id == 2;
	}

	public void setTempBan(){
		action_id = 2;
	}

	public int getActionID(){
		return action_id;
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
