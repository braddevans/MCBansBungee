package com.mcbans.rona_tombo.mcbansbungee.api;

import com.mcbans.rona_tombo.mcbansbungee.BanType;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.callback.AltLookupCallback;
import com.mcbans.rona_tombo.mcbansbungee.callback.BanLookupCallback;
import com.mcbans.rona_tombo.mcbansbungee.callback.LookupCallback;
import com.mcbans.rona_tombo.mcbansbungee.callback.MessageCallback;
import com.mcbans.rona_tombo.mcbansbungee.request.*;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;

public class MCBansBungeeAPI{
	private static HashMap<Plugin, MCBansBungeeAPI> apiHandles = new HashMap<>();
	private final MCBansBungee plugin;
	private final String pname;

	private MCBansBungeeAPI(final MCBansBungee plugin, final String pname){
		plugin.getLog().info("MCBans API linked with: " + pname);
		this.plugin = plugin;
		this.pname = pname;
	}

	public static MCBansBungeeAPI getHandle(final MCBansBungee plugin, final Plugin otherPlugin){
		if(otherPlugin == null){
			return null;
		}

		MCBansBungeeAPI api = apiHandles.get(otherPlugin);

		if(api == null){
			api = new MCBansBungeeAPI(plugin, otherPlugin.getDescription().getName());
			apiHandles.put(otherPlugin, api);
		}

		return api;
	}

	private void ban(BanType type, String targetName, String targetUUID, String senderName, String senderUUID, String reason, String duration, String measure){
		if(targetName == null || senderName == null){
			return;
		}
		String targetIP = "";
		if(type != BanType.UNBAN){
			final ProxiedPlayer target = plugin.getProxy().getPlayer(targetName);
			targetIP = (target != null) ? target.getAddress().getAddress().getHostAddress() : "";
		}

		Ban banControl = new Ban(plugin, type.getActionName(), targetName, targetUUID, targetIP, senderName, senderUUID, reason, duration, measure, null);
		Thread triggerThread = new Thread(banControl);
		triggerThread.start();
	}

	/**
	 * Add Locally BAN.
	 *
	 * @param targetName BAN target player's name.
	 * @param senderName BAN issued admin's name.
	 * @param reason     BAN reason.
	 */
	public void localBan(String targetName, String targetUUID, String senderName, String senderUUID, String reason){
		plugin.getLog().info("Plugin " + pname + " tried to local ban player " + targetName);

		reason = (reason == null || reason.equals("")) ? plugin.getConfigs().getDefaultLocal() : reason;
		ban(BanType.LOCAL, targetName, targetUUID, senderName, senderUUID, reason, "", "");
	}

	/**
	 * Add Globally BAN.
	 *
	 * @param targetName BAN target player's name.
	 * @param senderName BAN issued admin's name.
	 * @param reason     BAN reason.
	 */
	public void globalBan(String targetName, String targetUUID, String senderName, String senderUUID, String reason){
		plugin.getLog().info("Plugin " + pname + " tried to global ban player " + targetName);
		if(reason == null || reason.equals("")){
			plugin.getLog().warn("Plugin " + pname + " tried to global ban player " + targetName + "but it canceled because reason is empty.");
			return;
		}
		ban(BanType.GLOBAL, targetName, targetUUID, senderName, senderUUID, reason, "", "");
	}

	/**
	 * Add Temporary BAN.
	 *
	 * @param targetName BAN target player's name.
	 * @param senderName BAN issued admin's name.
	 * @param senderUUID Ban issued admin's UUID.
	 * @param reason     BAN reason.
	 * @param duration   Banning length duration (intValue).
	 * @param measure    Banning length measure (m(minute), h(hour), d(day), w(week)).
	 */
	public void tempBan(String targetName, String targetUUID, String senderName, String senderUUID, String reason, String duration, String measure){
		plugin.getLog().info("Plugin " + pname + " tried to temp ban player " + targetName);

		reason = (reason == null || reason.equals("")) ? plugin.getConfigs().getDefaultTemp() : reason;
		duration = duration.equals("") ? "" : duration;
		measure = measure.equals("") ? "" : measure;
		ban(BanType.TEMP, targetName, targetUUID, senderName, senderUUID, reason, duration, measure);
	}

	/**
	 * Remove BAN
	 *
	 * @param targetName UnBan target player's name.
	 * @param senderName UnBan issued admin's name.
	 * @param senderUUID UnBan issued admin's UUID.
	 */
	public void unBan(String targetName, String targetUUID, String senderName, String senderUUID){
		plugin.getLog().info("Plugin " + pname + " tried to unban player " + targetName);
		if(targetName == null || senderName == null){
			plugin.getLog().warn("Invalid usage (null): unBan");
			return;
		}
		if(! Util.isValidName(targetName) && ! Util.isValidIP(targetName)){
			plugin.getLog().info("The target you are trying to unban is not a valid name or IP format!");
			return;
		}

		ban(BanType.UNBAN, targetName, targetUUID, senderName, senderUUID, "", "", "");
	}

	/**
	 * Add IPBan.
	 *
	 * @param ip         target ip address.
	 * @param senderName IPBan issued admin's name.
	 * @param senderUUID IPBan issued admin's UUID.
	 * @param reason     Ban reason.
	 * @param callback   MessageCallback.
	 */
	public void ipBan(String ip, String senderName, String senderUUID, String reason, MessageCallback callback){
		plugin.getLog().info("Plugin " + pname + " tried to ip ban " + ip);
		if(ip == null || senderName == null || callback == null){
			plugin.getLog().info("Invalid usage (null): ipBan");
			return;
		}
		if(! Util.isValidIP(ip)){
			plugin.getLog().info("Invalid IP address (" + ip + "): ipBan");
			return;
		}
		if(reason == null || reason.length() <= 0){
			reason = plugin.getConfigs().getDefaultLocal();
		}

		BanIpRequest request = new BanIpRequest(plugin, callback, ip, reason, senderName, senderUUID);
		Thread thread = new Thread(request);
		thread.start();
	}

	/**
	 * Add IPBan.
	 *
	 * @param ip         target ip address.
	 * @param senderName IPBan issued admin's name.
	 * @param reason     Ban reason.
	 */
	public void IPBan(String ip, String senderName, String reason){
		ipBan(ip, senderName, "", reason, new MessageCallback(plugin));
	}

	/**
	 * Kick Player.
	 *
	 * @param targetName Kick target player's name.
	 * @param senderName Kick issued admin's name.
	 * @param reason     Kick reason.
	 */
	public void kick(String targetName, String targetUUID, String senderName, String senderUUID, String reason){
		reason = (reason == null || reason.equals("")) ? plugin.getConfigs().getDefaultKick() : reason;

		Kick kickPlayer = new Kick(plugin, targetName, targetUUID, senderName, senderUUID, reason);
		Thread triggerThread = new Thread(kickPlayer);
		triggerThread.start();
	}

	/**
	 * Lookup Player.
	 *
	 * @param targetName Lookup target player's name.
	 * @param senderName Lookup issued admin's name.
	 * @param senderUUID Lookup issued admin's UUID.
	 * @param callback   LookupCallback.
	 */
	public void lookupPlayer(String targetName, String targetUUID, String senderName, String senderUUID, LookupCallback callback){
		plugin.getLog().info("Plugin " + pname + " tried to lookup player " + targetName);
		if(targetName == null || callback == null){
			plugin.getLog().info("Invalid usage (null): lookupPlayer");
			return;
		}

		if(! Util.isValidName(targetName)){
			callback.error("Invalid lookup target name.");
		}

		LookupRequest request = new LookupRequest(plugin, callback, targetName, targetUUID, senderName, senderUUID);
		Thread triggerThread = new Thread(request);
		triggerThread.start();
	}

	/**
	 * Lookup Ban.
	 *
	 * @param banID    Lookup target ban ID.
	 * @param callback BanLookupCallback.
	 */
	public void lookupBan(int banID, BanLookupCallback callback){
		plugin.getLog().info("Plugin " + pname + " tried to ban lookup " + banID);
		if(banID < 0 || callback == null){
			plugin.getLog().info("Invalid usage (null): lookupBan");
			return;
		}

		BanLookupRequest request = new BanLookupRequest(plugin, callback, banID);
		Thread triggerThread = new Thread(request);
		triggerThread.start();
	}

	/**
	 * Lookup Alt Accounts.
	 *
	 * @param playerName Lookup target player name.
	 * @param callback   BanLookupCallback.
	 */
	public void lookupAlt(String playerName, AltLookupCallback callback){
		plugin.getLog().info("Plugin " + pname + " tried to alt lookup " + playerName);
		if(playerName == null || callback == null){
			plugin.getLog().info("Invalid usage (null): lookupAlt");
			return;
		}

		if(! Util.isValidName(playerName)){
			callback.error("Invalid alternate account lookup target name.");
		}

		AltLookupRequest request = new AltLookupRequest(plugin, callback, playerName);
		Thread triggerThread = new Thread(request);
		triggerThread.start();
	}

	/**
	 * Get MCBans plugin version.
	 *
	 * @return plugin version.
	 */
	public String getVersion(){
		return plugin.getDescription().getVersion();
	}
}
