package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.callback.PreviousCallback;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONException;
import org.json.JSONObject;

public class PreviousNames extends BaseRequest<PreviousCallback>{
	public String target;

	public PreviousNames(MCBansBungee plugin, PreviousCallback callback, String target, String targetUUID, String sender){
		super(plugin, callback);
		this.items.put("player", target);
		this.items.put("player_uuid", targetUUID);
		this.items.put("admin", sender);
		this.items.put("exec", "uuidLookup");
		this.target = (! target.equals("")) ? target : targetUUID;
	}

	@Override
	protected void execute(){
		if(callback.getSender() != null){
			log.info(callback.getSender().getName() + " performed a player history lookup for " + target + "!");
		}
		JSONObject result = this.request_JOBJ();
		try{
			callback.success(result.getString("player"), result.getString("players"));
		}catch(JSONException ex){
			if(result.toString().contains("error")){
				if(result.toString().contains("dne")){
					callback.error("Player record not found: " + target);
					return;
				}else if(result.toString().contains("Server Disabled")){
					ActionLog.getInstance().severe("This server has been disabled by MCBans staff.");
					ActionLog.getInstance().severe("To appeal this decision, please file ticket on forums.mcbans.com");

					callback.error("This server has been disabled by MCBans staff.");
					return;
				}
			}
			ActionLog.getInstance().severe("A JSON error occurred while trying to parse player name history data.");
			callback.error("An error occurred while parsing JSON data.");
		}catch(NullPointerException ex){
			ActionLog.getInstance().severe("Unable to reach MCBans API server!");
			callback.error(ChatColor.RED + "Unable to reach MCBans API server!");
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
