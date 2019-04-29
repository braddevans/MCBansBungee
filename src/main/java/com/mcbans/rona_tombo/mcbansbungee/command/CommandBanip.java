package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.callback.MessageCallback;
import com.mcbans.rona_tombo.mcbansbungee.exception.CommandException;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import com.mcbans.rona_tombo.mcbansbungee.request.BanIpRequest;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

public class CommandBanip extends BaseCommand{
	CommandBanip(){
		bePlayer = false;
		argLength = 1;
		banning = true;
	}

	@Override
	public void run() throws CommandException{
		args.remove(0);

		String reason = config.getDefaultLocal();
		if(args.size() > 0){
			reason = Util.join(args, " ");
		}
		if(! Util.isValidIP(target)){
			throw new CommandException(ChatColor.RED + localize("invalidIP"));
		}

		BanIpRequest request = new BanIpRequest(plugin, new MessageCallback(plugin, sender), target, reason, senderName, senderUUID);
		Thread triggerThread = new Thread(request);
		triggerThread.start();
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return Perms.BAN_IP.has(sender);
	}
}
