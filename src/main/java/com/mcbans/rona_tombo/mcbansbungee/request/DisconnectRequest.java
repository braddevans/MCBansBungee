package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.callback.MessageCallback;

import java.util.UUID;

public class DisconnectRequest extends BaseRequest<MessageCallback>{
	private long startTime;

	public DisconnectRequest(final MCBansBungee plugin, final String playerName, final UUID playerUUID){
		super(plugin, new MessageCallback(plugin, null));

		items.put("player", playerName);
		items.put("player_uuid", playerUUID.toString());
		items.put("exec", "playerDisconnect");
	}

	public DisconnectRequest(final MCBansBungee plugin, final String playerName){
		super(plugin, new MessageCallback(plugin, null));

		items.put("player", playerName);
		items.put("exec", "playerDisconnect");
	}

	@Override
	protected void execute(){
		request();
	}
}
