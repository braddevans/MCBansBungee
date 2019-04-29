package com.mcbans.rona_tombo.mcbansbungee.callback;

import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.CommandSender;

public class MessageCallback extends BaseCallback{
	private String message;
	private String bmessage;

	public MessageCallback(final MCBansBungee plugin, final CommandSender sender){
		super(plugin, sender);
	}

	public MessageCallback(final MCBansBungee plugin){
		super(plugin, null);
	}

	public void setMessage(final String message){
		this.message = message;
	}

	public void setBroadcastMessage(final String message){
		this.bmessage = message;
	}

	@Override
	public void success(){
		if(message != null && sender != null){
			Util.message(sender, message);
		}
		if(bmessage != null && bmessage.length() > 0){
			Util.broadcastMessage(bmessage);
		}
	}

	@Override
	public void error(String error){
		if(error != null && sender != null){
			Util.message(sender, error);
		}
	}
}
