package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.callback.AltLookupCallback;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import com.mcbans.rona_tombo.mcbansbungee.request.AltLookupRequest;
import net.md_5.bungee.api.CommandSender;

public class CommandAltlookup extends BaseCommand{
	CommandAltlookup(){
		bePlayer = false;
		argLength = 1;
		banning = true;
	}

	@Override
	public void run(){
		args.remove(0);

		AltLookupRequest request = new AltLookupRequest(plugin, new AltLookupCallback(plugin, sender), target);
		Thread triggerThread = new Thread(request);
		triggerThread.start();
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return Perms.LOOKUP_ALT.has(sender);
	}
}
