package com.mcbans.rona_tombo.mcbansbungee.permission;

import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;

public enum Perms{
	//Admin Permission
	ADMIN("admin"),

	// Ban Permission
	BAN_GLOBAL("ban.global"),
	BAN_LOCAL("ban.local"),
	BAN_TEMP("ban.temp"),
	BAN_IP("ban.ip"),
	UNBAN("unban"),
	KICK("kick"),

	// View Permission
	VIEW_ALTS("view.alts"),
	VIEW_BANS("view.bans"),
	VIEW_STAFF("view.staff"),
	VIEW_PREVIOUS("view.previous"),
	VIEW_PROXY("view.proxy"),
	VIEW_ANNOUNCE("announce"),
	HIDE_VIEW("hideview"),

	// Exempt
	EXEMPT_KICK("kick.exempt"),
	EXEMPT_BAN("ban.exempt"),
	EXEMPT_MAXALTS("maxalts.exempt"),

	// Others
	LOOKUP_PLAYER("lookup.player"),
	LOOKUP_BAN("lookup.ban"),
	LOOKUP_ALT("lookup.alt");

	final static String HEADER = "mcbans.";
	private static PermissionHandler handler = null;
	private String node;

	Perms(final String node){
		this.node = HEADER + node;
	}

	public static void setupPermissionHandler(){
		if(handler == null){
			handler = PermissionHandler.getInstance();
		}
	}

	public boolean has(String playerName){
		if(playerName == null){
			return false;
		}
		return handler.has(playerName, HEADER + node);
	}

	public boolean has(ProxiedPlayer player){
		return this.has(player.getName());
	}

	public boolean has(CommandSender sender){
		if(! (sender instanceof ProxiedPlayer)){
			return true;
		}
		return has((ProxiedPlayer) sender);
	}

	public void message(final String message){
		for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
			if(this.has(player)){
				Util.message(player, message);
			}
		}
	}

	public String getNode(){
		return node;
	}

	public Set<ProxiedPlayer> getPlayers(){
		Set<ProxiedPlayer> players = new HashSet<>();
		for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
			if(this.has(player)){
				players.add(player);
			}
		}
		return players;
	}

	public Set<String> getPlayerNames(){
		Set<String> names = new HashSet<>();
		for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
			names.add(player.getName());
		}
		return names;
	}
}
