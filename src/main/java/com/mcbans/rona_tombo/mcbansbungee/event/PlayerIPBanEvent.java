package com.mcbans.rona_tombo.mcbansbungee.event;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class PlayerIPBanEvent extends Event implements Cancellable{
	private boolean isCancelled = false;

	private String ip;
	private String sender;
	private String reason, senderUUID;

	public PlayerIPBanEvent(String ip, String sender, String senderUUID, String reason){
		this.ip = ip;
		this.sender = sender;
		this.senderUUID = senderUUID;
		this.reason = reason;
	}

	public UUID getSenderUUID(){
		return UUID.fromString(senderUUID);
	}

	public String getIp(){
		return ip;
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
