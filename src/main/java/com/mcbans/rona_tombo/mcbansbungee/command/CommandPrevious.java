package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.callback.PreviousCallback;
import com.mcbans.rona_tombo.mcbansbungee.exception.CommandException;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import com.mcbans.rona_tombo.mcbansbungee.request.PreviousNames;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

public class CommandPrevious extends BaseCommand{
	CommandPrevious(){
		bePlayer = false;
		argLength = 1;
		banning = true;
	}

	@Override
	public void run() throws CommandException{
		args.remove(0);
		if(! hasPermission(sender)){
			throw new CommandException(ChatColor.RED + localize("permissionDenied"));
		}

		(new Thread(new PreviousNames(plugin, new PreviousCallback(plugin, sender), target, targetUUID, senderName))).start();
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return Perms.VIEW_PREVIOUS.has(sender);
	}
}
