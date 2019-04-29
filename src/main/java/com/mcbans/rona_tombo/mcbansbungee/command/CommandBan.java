package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.BanType;
import com.mcbans.rona_tombo.mcbansbungee.exception.CommandException;
import com.mcbans.rona_tombo.mcbansbungee.request.Ban;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

public class CommandBan extends BaseCommand{
	CommandBan(){
		bePlayer = false;
		argLength = 1;
		banning = true;
	}

	@Override
	public void run() throws CommandException{

		args.remove(0);

		BanType type = BanType.LOCAL;
		if(args.size() > 0){
			if(args.get(0).equalsIgnoreCase("-g")){
				type = BanType.GLOBAL;
			}else if(args.get(0).equalsIgnoreCase("-t")){
				type = BanType.TEMP;
			}
		}
		if(type != BanType.LOCAL){
			args.remove(0);
		}

		if(! type.getPermission().has(senderName)){
			throw new CommandException(ChatColor.RED + localize("permissionDenied"));
		}

		String reason;
		Ban banControl = null;
		switch(type){
			case LOCAL:
				reason = config.getDefaultLocal();
				if(args.size() > 0){
					reason = Util.join(args, " ");
				}
				banControl = new Ban(plugin, type.getActionName(), target, targetUUID, targetIP, senderName, senderUUID, reason, "", "", null);
				break;
			case GLOBAL:
				if(args.size() == 0){
					Util.message(senderName, ChatColor.RED + localize("formatError"));
					return;
				}
				reason = Util.join(args, " ");
				banControl = new Ban(plugin, type.getActionName(), target, targetUUID, targetIP, senderName, senderUUID, reason, "", "", null);
				break;
			case TEMP:
				if(args.size() < 2){
					Util.message(sender, ChatColor.RED + localize("formatError"));
					return;
				}
				String measure = "";
				String duration = args.remove(0);
				if(! duration.matches("(?sim)([0-9]+)(minute(s|)|m|hour(s|)|h|day(s|)|d|week(s|)|w)")){
					measure = args.remove(0);
				}else{
					try{
						Pattern regex = Pattern.compile("([0-9]+)(minute(s|)|m|hour(s|)|h|day(s|)|d|week(s|)|w)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
						Matcher regexMatcher = regex.matcher(duration);
						if(regexMatcher.find()){
							duration = regexMatcher.group(1);
							measure = regexMatcher.group(2);
						}
					}catch(PatternSyntaxException ignore){
					}
				}
				reason = config.getDefaultTemp();
				if(args.size() > 0){
					reason = Util.join(args, " ");
				}
				banControl = new Ban(plugin, type.getActionName(), target, targetUUID, targetIP, senderName, senderUUID, reason, duration, measure, null);
				break;
		}

		if(banControl == null){
			Util.message(senderName, ChatColor.RED + "Internal error. Please report console logs to an MCBans developer.");
			throw new RuntimeException("Undefined BanType: " + type.name());
		}
		Thread triggerThread = new Thread(banControl);
		triggerThread.start();
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return (BanType.GLOBAL.getPermission().has(sender) ||
				BanType.LOCAL.getPermission().has(sender) ||
				BanType.TEMP.getPermission().has(sender));
	}
}
