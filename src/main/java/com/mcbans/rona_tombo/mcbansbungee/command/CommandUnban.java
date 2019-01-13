package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.BanType;
import com.mcbans.rona_tombo.mcbansbungee.request.Ban;
import net.md_5.bungee.api.CommandSender;

public class CommandUnban extends BaseCommand{
	CommandUnban(){
		bePlayer = false;
		argLength = 1;
		banning = true;
	}

	@Override
	public void run(){
		args.remove(0);

		Ban banControl = new Ban(plugin, BanType.UNBAN.getActionName(), target, targetUUID, "", senderName, senderUUID, "", "", "", null);
		Thread triggerThread = new Thread(banControl);
		triggerThread.start();
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return BanType.UNBAN.getPermission().has(sender);
	}
}
