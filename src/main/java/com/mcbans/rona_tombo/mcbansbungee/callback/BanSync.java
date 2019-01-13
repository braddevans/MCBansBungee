package com.mcbans.rona_tombo.mcbansbungee.callback;

import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.request.JsonHandler;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class BanSync implements Runnable{
	private final MCBansBungee plugin;

	public BanSync(MCBansBungee plugin){
		this.plugin = plugin;
	}

	@Override
	public void run(){
		while(true){
			int syncInterval = ((60 * 1000) * plugin.getConfigs().getSyncInterval());
			if(syncInterval < ((60 * 1000) * 60)){
				syncInterval = ((60 * 1000) * 60);
			}
			while(plugin.apiServer == null){
				try{
					Thread.sleep(5000);
				}catch(InterruptedException ignore){
				}
			}

			if(plugin.getConfigs().isEnableAutoSync()){

				plugin.lastSync = System.currentTimeMillis() / 1000;
			}

			try{
				Thread.sleep(syncInterval);
			}catch(InterruptedException ignore){
			}
		}
	}

	public void goRequest(){
		startSync();
	}

	public void startSync(){
		if(plugin.syncRunning){
			return;
		}
		plugin.syncRunning = true;

		try{
			boolean goNext = true;

			while(goNext){
				if(String.valueOf(plugin.lastType).equals("") || String.valueOf(plugin.lastType) == null || String.valueOf(plugin.lastID).equals("")){
					plugin.lastType = "bans";
					plugin.lastID = 0;
					goNext = false;
					System.out.println(ChatColor.RED + "MCBans: Error resetting sync. Please report this to an MCBans developer.");
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
								}
							}
						}
						if(response.has("lastid")){
							if(response.getLong("lastid") == 0 && plugin.lastType.equalsIgnoreCase("bans")){
								plugin.lastType = "sync";
								plugin.lastID = 0;
								plugin.debug("Bans have been retrieved!");
							}else if(plugin.lastID == response.getLong("lastud") && plugin.lastType.equalsIgnoreCase("sync")){
								plugin.debug("Sync has completed!");
								goNext = false;
							}else{
								plugin.debug("Received " + plugin.lastType + "from " + plugin.lastID + "to " + response.getLong("lastid"));
								plugin.lastID = response.getLong("lastid");
							}
						}
						save();
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
		save();
	}

	private void save(){
		plugin.lastSyncs.setProperty("lastId", String.valueOf(plugin.lastID));
		plugin.lastSyncs.setProperty("lastType", String.valueOf(plugin.lastType));
		try{
			plugin.lastSyncs.store(new FileOutputStream(plugin.syncIni), "Syncing ban information.");
		}catch(FileNotFoundException ex){
			if(plugin.getConfigs().isDebug()){
				ex.printStackTrace();
			}
		}catch(IOException ex){
			if(plugin.getConfigs().isDebug()){
				ex.getMessage();
			}
		}
	}
}
