package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.BanType;
import com.mcbans.rona_tombo.mcbansbungee.request.Ban;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CommandTempban extends BaseCommand{
	CommandTempban(){
		bePlayer = false;
		argLength = 2;
		banning = true;
	}

	@Override
	public void run(){
		args.remove(0);

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

		String reason = config.getDefaultTemp();
		if(args.size() > 0){
			reason = Util.join(args, " ");
		}

		// Start

		Ban banControl = new Ban(plugin, BanType.TEMP.getActionName(), target, targetUUID, targetIP, senderName, senderUUID, reason, duration, measure, null);
		Thread triggerThread = new Thread(banControl);
		triggerThread.start();
	}

	@Override
	public boolean hasPermission(CommandSender sender){
		return BanType.TEMP.getPermission().has(sender);
	}
}
