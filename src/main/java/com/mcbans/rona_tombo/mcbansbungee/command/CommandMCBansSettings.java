package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.callback.MCBansSettingsCallback;
import com.mcbans.rona_tombo.mcbansbungee.exception.CommandException;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import com.mcbans.rona_tombo.mcbansbungee.request.MCBansSettings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

public class CommandMCBansSettings extends BaseCommand{
	CommandMCBansSettings(){
		bePlayer = false;
		argLength = 0;
		banning = true;
	}

	@Override
	public void run() throws CommandException{
		if(! hasPermission(sender)){
			throw new CommandException(ChatColor.RED + localize("permissionDenied"));
		}
		if(args.size() >= 2){
			(new Thread(new MCBansSettings(plugin, new MCBansSettingsCallback(plugin, sender), sender.getName(), args.toString()))).start();
		}else{
			throw new CommandException(ChatColor.RED + localize("formatError"));
		}
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return Perms.ADMIN.has(sender);
	}
}
