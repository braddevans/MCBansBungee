package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.api.data.AltLookupData;
import com.mcbans.rona_tombo.mcbansbungee.callback.AltLookupCallback;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class AltLookupRequest extends BaseRequest<AltLookupCallback>{
	private String playerName;
	private UUID playerUUID;

	public AltLookupRequest(final MCBansBungee plugin, final AltLookupCallback callback, final UUID playerUUID, final String playerName){
		super(plugin, callback);

		this.items.put("player_uuid", playerUUID.toString());
		this.items.put("player", playerName);
		this.items.put("exec", "altList");

		this.playerUUID = playerUUID;
		this.playerName = playerName;
	}

	public AltLookupRequest(final MCBansBungee plugin, final AltLookupCallback callback, final String playerName){
		super(plugin, callback);

		this.items.put("player", playerName);
		this.items.put("exec", "altList");

		this.playerName = playerName;
	}

	@Override
	protected void execute(){
		if(callback.getSender() != null){
			log.info(callback.getSender().getName() + " has looked up " + playerName + "'s alternate accounts.");
		}
		JSONObject result = this.request_JOBJ();

		try{
			if(result != null && result.has("result") && result.getString("result").trim().equals("n")){
				callback.error("This server is not premium.");
			}else{
				callback.success(new AltLookupData(playerName, result));
			}
		}catch(JSONException ex){
			if(result.toString().contains("error")){
				if(result.toString().contains("Server Disabled")){
					ActionLog.getInstance().severe("This server has been disabled by MCBans staff.");
					ActionLog.getInstance().severe("To appeal this decision, please file a ticket at forums.mcbans.com.");

					callback.error("This server has been disabled by MCBans staff.");
					return;
				}
			}
			ActionLog.getInstance().severe("A JSON error occurred while trying to parse alternate account data.");
			callback.error("An error occurred while parsing JSON data.");
			if(plugin.getConfigs().isDebug()){
				ex.printStackTrace();
			}
		}catch(NullPointerException ex){
			ActionLog.getInstance().severe("Unable to reach MCBans server.");
			callback.error(ChatColor.RED + "Unable to reach MCBans server.");
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
