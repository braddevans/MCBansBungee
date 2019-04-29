package com.mcbans.rona_tombo.mcbansbungee.callback;

import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.api.data.PlayerLookupData;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class LookupCallback extends BaseCallback{
	public LookupCallback(final MCBansBungee plugin, final CommandSender sender){
		super(plugin, sender);
	}

	public LookupCallback(){
		super(MCBansBungee.getInstance(), null);
	}

	public void success(final PlayerLookupData data){
		if(data.getGlobals().size() > 0 || data.getLocals().size() > 0 || data.getOthers().size() > 0){
			Util.message(sender, ChatColor.DARK_GRAY + "------------------------------");
		}

		Util.message(sender, ChatColor.GRAY + "Player " + ChatColor.RED + data.getPlayerName() + ChatColor.GRAY + " has " + ChatColor.RED
				+ data.getTotal() + " ban(s)" + ChatColor.GRAY + " and " + ChatColor.BLUE + data.getReputation() + " REP"
				+ ChatColor.GRAY + ".");

		if(data.getGlobals().size() > 0){
			Util.message(sender, ChatColor.RED + "Global Bans:");
			for(String record : data.getGlobals()){
				Util.message(sender, record);
			}
		}
		if(data.getLocals().size() > 0){
			Util.message(sender, ChatColor.GOLD + "Local Bans:");
			for(String record : data.getLocals()){
				Util.message(sender, record);
			}
		}
		if(data.getOthers().size() > 0){
			for(String record : data.getOthers()){
				Util.message(sender, record);
			}
		}

		if(data.getGlobals().size() > 0 || data.getLocals().size() > 0 || data.getOthers().size() > 0){
			Util.message(sender, ChatColor.DARK_GRAY + "------------------------------");
		}
	}

	@Override
	public void success(){
		throw new IllegalArgumentException("Wrong Usage!");
	}

	@Override
	public void error(final String error){
		if(error != null && sender != null){
			Util.message(sender, ChatColor.RED + error);
		}
	}
}
