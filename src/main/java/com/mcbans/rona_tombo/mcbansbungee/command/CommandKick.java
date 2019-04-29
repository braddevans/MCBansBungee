package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import com.mcbans.rona_tombo.mcbansbungee.request.Kick;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.CommandSender;

public class CommandKick extends BaseCommand{

	CommandKick(){
		bePlayer = false;
		argLength = 1;
		banning = true;
	}

	@Override
	public void run(){

		args.remove(0);

		String reason = config.getDefaultKick();
		if(args.size() > 0){
			reason = Util.join(args, " ");
		}

		Kick kickPlayer = new Kick(plugin, target, targetUUID, senderName, senderUUID, reason);
		Thread triggerThread = new Thread(kickPlayer);
		triggerThread.start();
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return Perms.KICK.has(sender);
	}
}
