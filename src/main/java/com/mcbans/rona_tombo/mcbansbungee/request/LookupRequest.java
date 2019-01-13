package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.api.data.PlayerLookupData;
import com.mcbans.rona_tombo.mcbansbungee.callback.LookupCallback;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONException;
import org.json.JSONObject;

public class LookupRequest extends BaseRequest<LookupCallback>{
	private String targetName;

	public LookupRequest(final MCBansBungee plugin, final LookupCallback callback, final String playerName,
	                     final String playerUUID, final String senderName, String senderUUID){
		super(plugin, callback);
		if(! playerUUID.equals("")){
			this.items.put("player_uuid", playerUUID);
		}else{
			this.items.put("player", playerName);
		}
		this.items.put("admin", senderName);
		this.items.put("admin_uuid", senderUUID);
		this.items.put("exec", "playerLookup");

		this.targetName = playerName;
	}

	public LookupRequest(final MCBansBungee plugin, final LookupCallback callback, final String playerName, final String senderName){
		super(plugin, callback);

		this.items.put("player", playerName);
		this.items.put("admin", senderName);
		this.items.put("exec", "playerLookup");

		this.targetName = playerName;
	}

	@Override
	protected void execute(){
		if(callback.getSender() != null){
			log.info(callback.getSender().getName() + " has looked up the " + targetName + "!");
		}

		JSONObject result = this.request_JOBJ();

		try{
			callback.success(new PlayerLookupData(targetName, result));
		}catch(JSONException ex){
			if(result.toString().contains("error")){
				if(result.toString().contains("Server Disabled")){
					ActionLog.getInstance().severe("This server has been disabled by MCBans staff.");
					ActionLog.getInstance().severe("To appeal this decision, please file a ticket at forums.mcbans.com.");

					callback.error("This server has been disabled by MCBans staff.");
					return;
				}
			}
			ActionLog.getInstance().severe("A JSON error occurred while trying to parse lookup data.");
			callback.error("An error occurred while parsing JSON data.");
		}catch(NullPointerException ex){
			ActionLog.getInstance().severe("Unable to reach the MCBans API!");
			callback.error(ChatColor.RED + "Unable to reach the MCBans API!");
			if(plugin.getConfigs().isDebug()){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			callback.error("Unknown Error: " + ex.getMessage());
			if(plugin.getConfigs().isDebug()){
				ex.printStackTrace();
			}
		}
	}
}
