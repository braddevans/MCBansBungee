package com.mcbans.rona_tombo.mcbansbungee.event;

import net.md_5.bungee.api.plugin.Event;

public class PlayerBannedEvent extends Event{
	private String player;
	private String playerIP;
	private String sender;
	private String reason;
	private int action_id;
	private String duration;
	private String measure;

	public PlayerBannedEvent(String player, String playerIP, String sender, String reason, int action_id, String duration, String measure){
		this.player = player;
		this.playerIP = playerIP;
		this.sender = sender;
		this.reason = reason;
		this.action_id = action_id;
		this.duration = duration;
		this.measure = measure;
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

	public String getReason(){
		return reason;
	}

	public String getDuration(){
		return duration;
	}

	public String getMeasure(){
		return measure;
	}

	public boolean isGlobalBan(){
		return action_id == 0;
	}

	public boolean isLocalban(){
		return action_id == 1;
	}

	public boolean isTempBan(){
		return action_id == 2;
	}
}
