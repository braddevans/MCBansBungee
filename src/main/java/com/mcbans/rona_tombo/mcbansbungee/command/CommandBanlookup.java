package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.callback.BanLookupCallback;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import com.mcbans.rona_tombo.mcbansbungee.request.BanLookupRequest;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.CommandSender;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

public class CommandBanlookup extends BaseCommand{
	CommandBanlookup(){
		bePlayer = false;
		argLength = 1;
		banning = false;
	}

	@Override
	public void run(){
		target = args.remove(0);

		if(! Util.isInteger(target) || Integer.parseInt(target) < 0){
			Util.message(sender, localize("formatError"));
			return;
		}

		BanLookupRequest request = new BanLookupRequest(plugin, new BanLookupCallback(plugin, sender), Integer.parseInt(target));
		Thread triggerThread = new Thread(request);
		triggerThread.start();
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return Perms.LOOKUP_BAN.has(sender);
	}
}
