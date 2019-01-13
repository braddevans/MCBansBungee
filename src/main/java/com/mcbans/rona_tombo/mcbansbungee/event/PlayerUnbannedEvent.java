package com.mcbans.rona_tombo.mcbansbungee.event;

import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class PlayerUnbannedEvent extends Event{
	private String player;
	private String sender, senderUUID, playerUUID;

	public PlayerUnbannedEvent(String player, String playerUUID, String sender, String senderUUID){
		this.player = player;
		this.sender = sender;
		this.senderUUID = senderUUID;
		this.playerUUID = playerUUID;
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

	public String getSenderName(){
		return sender;
	}
}
