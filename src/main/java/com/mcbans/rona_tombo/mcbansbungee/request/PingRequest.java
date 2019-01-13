package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.callback.MessageCallback;
import net.md_5.bungee.api.ChatColor;

public class PingRequest extends BaseRequest<MessageCallback>{
	private long startTime;

	public PingRequest(final MCBansBungee plugin, final MessageCallback callback){
		super(plugin, callback);

		this.items.put("exec", "check");
		startTime = System.currentTimeMillis();
	}

	@Override
	protected void execute(){
		if("up".equals(request_String())){
			callback.setMessage(ChatColor.GREEN + "API Response Time: " + ((System.currentTimeMillis()) - startTime) + " milliseconds!");
			callback.success();
		}else{
			callback.error(ChatColor.RED + "MCBans API appears to be down or unreachable! Please notify MCBans staff!");
		}
	}
}
