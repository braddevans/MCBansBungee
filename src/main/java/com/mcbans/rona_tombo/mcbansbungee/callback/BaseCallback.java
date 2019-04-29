package com.mcbans.rona_tombo.mcbansbungee.callback;

import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import net.md_5.bungee.api.CommandSender;

public abstract class BaseCallback{
	protected final MCBansBungee plugin;
	protected final CommandSender sender;

	BaseCallback(final MCBansBungee plugin, final CommandSender sender){
		this.plugin = plugin;
		this.sender = sender;
	}

	public abstract void success();

	public abstract void error(final String error);

	public CommandSender getSender(){
		return sender;
	}

	public void success(String identifier, String playerList){
	}
}
