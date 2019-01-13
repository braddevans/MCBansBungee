package com.mcbans.rona_tombo.mcbansbungee.callback;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import com.mcbans.rona_tombo.mcbansbungee.request.JsonHandler;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;

public class MainCallBack implements Runnable{
	private final MCBansBungee plugin;
	private final ActionLog log;
	public long last_req = 0;

	public MainCallBack(MCBansBungee plugin){
		this.plugin = plugin;
		this.log = plugin.getLog();
	}

	@Override
	public void run(){
		int callBackInterval = ((60 * 1000) * plugin.getConfigs().getCallBackInterval());
		if(callBackInterval < ((60 * 1000) * 15)){
			callBackInterval = ((60 * 1000) * 15);
		}

		while(true){
			while(plugin.apiServer == null){
				//waiting for server select
				try{
					Thread.sleep(1000);
				}catch(InterruptedException ignore){
				}
			}
			this.mainRequest();
			plugin.lastCallBack = System.currentTimeMillis() / 1000;
			try{
				Thread.sleep(callBackInterval);
			}catch(InterruptedException ignore){
			}
		}
	}

	public void goRequest(){
		mainRequest();
	}

	private void mainRequest(){
		JsonHandler webHandle = new JsonHandler(plugin);
		HashMap<String, String> url_items = new HashMap<>();
		url_items.put("maxPlayers", String.valueOf(50));
		url_items.put("version", "4.4.3b");
		url_items.put("exec", "callBack");
		HashMap<String, String> response = webHandle.mainRequest(url_items);
		try{
			if(response.containsKey("hasNotices")){
				for(String cb : response.keySet()){
					if(cb.contains("notice")){
						Perms.VIEW_BANS.message(ChatColor.GOLD + "Notice: " + ChatColor.WHITE + response.get(cb));
						log.info("MCBans Notice: " + response.get(cb));
					}
				}
			}
		}catch(NullPointerException ex){
			if(plugin.getConfigs().isDebug()){
				ex.printStackTrace();
			}
		}
	}
}
