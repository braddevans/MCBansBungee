package com.mcbans.rona_tombo.mcbansbungee.event;

import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class PlayerUnbanEvent extends Event implements Cancellable{
	private boolean isCancelled = false;

	private String target, targetUUID;
	private String sender, senderUUID;

	public PlayerUnbanEvent(String target, String targetUUID, String sender, String senderUUID){
		this.target = target;
		this.sender = sender;
		this.senderUUID = senderUUID;
		this.targetUUID = targetUUID;
	}

	public UUID getSenderUUID(){
		return UUID.fromString(senderUUID);
	}

	public String getTargetName(){
		return target;
	}

	@Deprecated
	public String getPlayerName(){
		return getTargetName();
	}

	public String getSenderName(){
		return sender;
	}

	public void setSenderName(String senderName){
		sender = senderName;
	}

	public boolean isIPBan(){
		return Util.isValidIP(target);
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
