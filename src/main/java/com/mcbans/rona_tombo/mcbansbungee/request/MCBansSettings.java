package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.callback.MCBansSettingsCallback;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONException;
import org.json.JSONObject;

public class MCBansSettings extends BaseRequest<MCBansSettingsCallback>{
	public String sender;
	private String commands;

	public MCBansSettings(MCBansBungee plugin, MCBansSettingsCallback callback, String sender, String commands){
		super(plugin, callback);
		this.commands = commands;
		this.sender = sender;
		this.items.put("admin", sender);
		this.items.put("setting", commands);
		this.items.put("exec", "setting");
	}

	@Override
	protected void execute(){
		if(callback.getSender() != null){
			log.info(callback.getSender().getName() + " executed setting change <" + commands + ">!");
		}
		JSONObject result = this.request_JOBJ();
		try{
			callback.success(result.getString("result"), result.getString("reason"));
		}catch(JSONException ex){
			if(result.toString().contains("error")){
				if(result.toString().contains("dne")){
					callback.error("Could not execute settings change: <" + commands + ">");
					return;
				}else if(result.toString().contains("Server Disabled")){
					ActionLog.getInstance().severe("This server has been disabled by MCBans staff.");
					ActionLog.getInstance().severe("To appeal this decision, please file a ticket at forums.mcbans.com.");

					callback.error("This server has been disabled by MCBans staff.");
					return;
				}
			}
			ActionLog.getInstance().severe("A JSON error occurred while trying to change server settings.");
			callback.error("An error occurred while parsing JSON data.");
		}catch(NullPointerException ex){
			ActionLog.getInstance().severe("Unable to reach the MCBans API server!");
			callback.error(ChatColor.RED + "Unable to reach the MCBans API server!");
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
