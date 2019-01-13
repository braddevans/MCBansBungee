package com.mcbans.rona_tombo.mcbansbungee.callback;

import com.mcbans.rona_tombo.mcbansbungee.I18n;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

public class PreviousCallback extends BaseCallback{
	public PreviousCallback(MCBansBungee plugin, CommandSender sender){
		super(plugin, sender);
	}

	@Override
	public void success(String identifier, String playerlist){
		if(! playerlist.equals("")){
			Util.message(sender, ChatColor.RED + localize("previousNamesHas", I18n.PLAYER, identifier, I18n.PLAYERS, playerlist));
		}else{
			Util.message(sender, ChatColor.AQUA + localize("previousNamesNone", I18n.PLAYER, identifier));
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
