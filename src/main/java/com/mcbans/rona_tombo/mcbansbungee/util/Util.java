package com.mcbans.rona_tombo.mcbansbungee.util;

import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util{
	private static final String IP_PATTERN =
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public static void message(final CommandSender target, String msg){
		if(msg != null){
			msg = MCBansBungee.getPrefix() + ChatColor.WHITE + msg;
			if(target instanceof ProxiedPlayer){
				target.sendMessage(TextComponent.fromLegacyText(msg));
			}else{
				ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(msg));
			}
		}
	}

	public static void message(final String playerName, String msg){
		final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(playerName);
		message(target, msg);
	}

	public static void broadcastMessage(String msg){
		for(ProxiedPlayer player : MCBansBungee.getInstance().getProxy().getPlayers()){
			if(Perms.VIEW_ANNOUNCE.has(player) || MCBansBungee.AnnounceAll){
				player.sendMessage(TextComponent.fromLegacyText(MCBansBungee.getPrefix() + msg));
			}
		}
	}

	public static String color(String msg){
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String join(Collection<?> s, String delimiter){
		StringBuilder builder = new StringBuilder();
		Iterator<?> iterator = s.iterator();

		while(iterator.hasNext()){
			builder.append(iterator.next());
			if(iterator.hasNext()){
				builder.append(delimiter);
			}
		}
		return builder.toString();
	}

	public static boolean isValidName(final String name){
		if(name == null){
			return false;
		}

		final String regex = "^[A-Za-z0-9_]{2,16}$";
		return Pattern.compile(regex).matcher(name).matches();
	}

	public static boolean isValidUUID(final String uuid){
		if(uuid == null){
			return false;
		}

		final String regex = "^[A-Za-z0-9_]{32}$";

		return Pattern.compile(regex).matcher(uuid.replaceAll("(?im)-", "")).matches();
	}

	public static boolean isInteger(String s){
		try{
			Integer.parseInt(s);
		}catch(NumberFormatException ex){
			return false;
		}
		return true;
	}

	public static boolean isDouble(String s){
		try{
			Double.parseDouble(s);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}

	public static boolean isValidIP(String s){
		if(s == null){
			return false;
		}
		Matcher matcher = Pattern.compile(IP_PATTERN).matcher(s);
		return matcher.matches();
	}
}
