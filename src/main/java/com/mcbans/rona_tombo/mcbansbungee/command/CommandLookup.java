package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.callback.LookupCallback;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import com.mcbans.rona_tombo.mcbansbungee.request.LookupRequest;
import net.md_5.bungee.api.CommandSender;

public class CommandLookup extends BaseCommand{
	public CommandLookup(){
		bePlayer = false;
		argLength = 1;
		banning = true;
	}

	@Override
	public void run(){
		args.remove(0);

		LookupRequest request = new LookupRequest(plugin, new LookupCallback(plugin, sender), target, targetUUID, senderName, senderUUID);
		Thread triggerThread = new Thread(request);
		triggerThread.start();
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return Perms.LOOKUP_PLAYER.has(sender);
	}
}
