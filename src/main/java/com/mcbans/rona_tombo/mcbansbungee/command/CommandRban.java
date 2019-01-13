package com.mcbans.rona_tombo.mcbansbungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandRban extends BaseCommand{
	CommandRban(){
		bePlayer = false;
		argLength = 0;
		banning = false;
	}

	@Override
	public void run(){
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "This command cannot use with MCBans BungeeCord Edition"));
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return true;
	}
}
