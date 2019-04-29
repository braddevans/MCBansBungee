package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.BanType;
import com.mcbans.rona_tombo.mcbansbungee.request.Ban;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.CommandSender;

public class CommandGlobanban extends BaseCommand{
	CommandGlobanban(){
		bePlayer = false;
		argLength = 2;
		banning = true;
	}

	@Override
	public void run(){
		args.remove(0);

		String reason = Util.join(args, " ");

		Ban banControl = new Ban(plugin, BanType.GLOBAL.getActionName(), target, targetUUID, targetIP, senderName, senderUUID, reason, "", "", null);
		Thread triggerThread = new Thread(banControl);
		triggerThread.start();
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return BanType.GLOBAL.getPermission().has(sender);
	}
}
