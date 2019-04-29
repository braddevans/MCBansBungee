package com.mcbans.rona_tombo.mcbansbungee.callback;

import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.request.JsonHandler;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ManualSync implements Runnable{
	private final MCBansBungee plugin;
	private final String commandSend;

	public ManualSync(final MCBansBungee plugin, final String sender){
		this.plugin = plugin;
		this.commandSend = sender;
	}

	@Override
	public void run(){
		while(plugin.apiServer == null){
			try{
				Thread.sleep(5000);
			}catch(InterruptedException ignore){
			}
		}

		if(plugin.syncRunning){
			Util.message(commandSend, ChatColor.GREEN + "Sync is already running.");
			return;
		}
		plugin.syncRunning = true;
		int changes = 0;
		try{
			boolean goNext = true;
			while(goNext){
				if(String.valueOf(plugin.lastType).equals("") || String.valueOf(plugin.lastType) == null || String.valueOf(plugin.lastID).equals("")){
					plugin.lastType = "bans";
					plugin.lastID = 0;
					goNext = false;
				}else{
					JsonHandler webHandle = new JsonHandler(plugin);
					HashMap<String, String> url_items = new HashMap<>();
					url_items.put("lastId", String.valueOf(plugin.lastID));
					url_items.put("lastType", String.valueOf(plugin.lastType));
					url_items.put("exec", "banSync");
					JSONObject response = webHandle.hdl_jobj(url_items);
					try{
						if(response.has("actions")){
							if(response.getJSONArray("actions").length() > 0){
								for(int v = 0; v < response.getJSONArray("actions").length(); v++){
									JSONObject plyer = response.getJSONArray("actions").getJSONObject(v);
									String name = plyer.getString("name");
									if(plugin.getBanManager().isBanned(name)){
										if(plyer.getString("do").equals("unban")){
											plugin.getBanManager().pardon(name);
										}
									}else{
										if(plyer.getString("do").equals("ban")){
											plugin.getBanManager().ban(name);
										}
									}

									changes++;
								}
							}
						}
						if(response.has("lastid")){
							if(response.getLong("lastid") == 0 && plugin.lastType.equalsIgnoreCase("bans")){
								plugin.lastType = "sync";
								plugin.lastID = 0;
								plugin.debug("Bans have been retrieved");
							}else if(plugin.lastID == response.getLong("lastid") && plugin.lastType.equalsIgnoreCase("sync")){
								plugin.debug("Sync has completed.");
								plugin.lastID = response.getLong("lastid");
							}
						}
					}catch(JSONException | NullPointerException ex){
						if(plugin.getConfigs().isDebug()){
							ex.printStackTrace();
						}
					}
					try{
						Thread.sleep(5000);
					}catch(InterruptedException ignore){
					}
				}
			}
		}finally{
			plugin.syncRunning = false;
		}
		plugin.lastSync = System.currentTimeMillis() / 1000;
		Util.message(commandSend, ChatColor.GREEN + "Sync is complete with " + changes + " actions.");
		save();
	}

	private void save(){
		plugin.lastSyncs.setProperty("lastId", String.valueOf(plugin.lastID));
		plugin.lastSyncs.setProperty("lastType", String.valueOf(plugin.lastType));
		try{
			plugin.lastSyncs.store(new FileOutputStream(plugin.syncIni), "Syncing ban information.");
		}catch(IOException e){
			if(plugin.getConfigs().isDebug()){
				e.printStackTrace();
			}
		}
	}
}
