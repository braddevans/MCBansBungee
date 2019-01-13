package com.mcbans.rona_tombo.mcbansbungee.event;

import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class PlayerIPBannedEvent extends Event{
	private String ip;
	private String sender;
	private String reason, senderUUID;

	public PlayerIPBannedEvent(String ip, String sender, String senderUUID, String reason){
		this.ip = ip;
		this.sender = sender;
		this.reason = reason;
		this.senderUUID = senderUUID;
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

	public String getReason(){
		return reason;
	}
}
