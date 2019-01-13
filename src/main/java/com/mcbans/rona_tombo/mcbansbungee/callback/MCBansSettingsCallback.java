package com.mcbans.rona_tombo.mcbansbungee.callback;

import com.mcbans.rona_tombo.mcbansbungee.I18n;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

public class MCBansSettingsCallback extends BaseCallback{
	public MCBansSettingsCallback(MCBansBungee plugin, CommandSender sender){
		super(plugin, sender);
	}

	@Override
	public void success(String returnData, String reason){
		if(returnData.equalsIgnoreCase("y")){
			Util.message(sender, ChatColor.GREEN + localize("successSetting", I18n.REASON, reason));
		}else{
			Util.message(sender, ChatColor.RED + localize("failSetting", I18n.REASON, reason));
		}
	}

	@Override
	public void success(){
		throw new IllegalArgumentException("Wrong Usage!");
	}

	@Override
	public void error(String error){
		if(error != null && sender != null){
			Util.message(sender, ChatColor.RED + error);
		}
	}
}
