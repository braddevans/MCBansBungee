package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.api.data.BanLookupData;
import com.mcbans.rona_tombo.mcbansbungee.callback.BanLookupCallback;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONException;
import org.json.JSONObject;

public class BanLookupRequest extends BaseRequest<BanLookupCallback>{
	private int banID;

	public BanLookupRequest(final MCBansBungee plugin, final BanLookupCallback callback, final int banID){
		super(plugin, callback);
		items.put("ban", String.valueOf(banID));
		items.put("exec", "banLookup");

		this.banID = banID;
	}

	@Override
	protected void execute(){
		if(callback.getSender() != null){
			log.info(callback.getSender().getName() + " has performed a ban lookup for ID " + banID + "!");
		}

		JSONObject result = request_JOBJ();

		try{
			callback.success(new BanLookupData(banID, result));
		}catch(JSONException ex){
			if(result.toString().contains("error")){
				if(result.toString().contains("dne")){
					callback.error("Ban record not found: " + banID);
					return;
				}else if(result.toString().contains("Server Disabled")){
					ActionLog.getInstance().severe("This server has been disabled by MCBans staff.");
					ActionLog.getInstance().severe("To appeal this decision, please file a ticket at forums.mcbans.com.");

					callback.error("This server has been disabled by MCBans staff.");
					return;
				}
			}
			ActionLog.getInstance().severe("A JSON error occurred while trying to parse ban lookup data.");
			callback.error("An error occurred while parsing JSON data.");
		}catch(NullPointerException ex){
			ActionLog.getInstance().severe("Unable to reach MCBans API.");
			callback.error(ChatColor.RED + "Unable to reach MCBans API.");
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
