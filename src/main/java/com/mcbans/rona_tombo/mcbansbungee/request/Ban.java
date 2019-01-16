package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.I18n;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.event.*;
import com.mcbans.rona_tombo.mcbansbungee.listener.PlayerListener;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

public class Ban implements Runnable{
	private final MCBansBungee plugin;
	private final ActionLog log;

	private String playerName, playerIP, senderName, reason, action, duration, measure, badword, playerUUID, senderUUID = null;
	private JSONObject actionData;
	private HashMap<String, Integer> responses = new HashMap<>();
	private int action_id;

	public Ban(MCBansBungee plugin, String action, String playerName, String playerUUID, String playerIP, String senderName, String senderUUID, String reason, String duration, String measure, JSONObject actionData){
		this(plugin, action, playerName, playerIP, senderName, reason, duration, measure, actionData);
		this.playerUUID = playerUUID;
		this.senderUUID = senderUUID;
	}

	private Ban(MCBansBungee plugin, String action, String playerName, String playerIP, String senderName, String reason, String duration, String measure, JSONObject actionData){
		this.plugin = plugin;
		this.log = plugin.getLog();

		this.playerName = playerName;
		this.playerIP = playerIP;
		this.senderName = senderName;
		this.reason = reason;
		this.duration = duration;
		this.measure = measure;
		this.action = action;
		this.actionData = (actionData != null) ? actionData : new JSONObject();
		String res = PlayerListener.cache.getIfPresent(playerName.toLowerCase());
		if(res != null){
			PlayerListener.cache.invalidate(playerName.toLowerCase());
		}
		responses.put("globalBan", 0);
		responses.put("localBan", 1);
		responses.put("tempBan", 2);
		responses.put("unBan", 3);
	}

	public Ban(MCBansBungee plugin, String action, String playerName, String playerIP, String senderName, String reason, String duration, String measure){
		this(plugin, action, playerName, playerIP, senderName, reason, duration, measure, null);
	}

	public void kickPlayer(String playerName, String playerUUID, final String kickReason){
		ProxiedPlayer targetTemp;
		if(! playerUUID.equals("")){
			targetTemp = plugin.getProxy().getPlayer(playerUUID);
		}else{
			targetTemp = plugin.getProxy().getPlayer(playerName);
		}
		final ProxiedPlayer target = targetTemp;
		if(target != null){
			plugin.getProxy().getScheduler().schedule(plugin, () -> target.disconnect(TextComponent.fromLegacyText(kickReason)), 0L, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void run(){
		while(plugin.apiServer == null){
			try{
				Thread.sleep(1000);
			}catch(InterruptedException ignore){
			}
		}
		if(responses.containsKey(action)){
			action_id = responses.get(action);
			if(action_id != 3){
				PlayerBanEvent banEvent = new PlayerBanEvent(playerName, playerUUID, playerIP, senderName, senderUUID, reason, action_id, duration, measure);
				plugin.getProxy().getPluginManager().callEvent(banEvent);
				if(banEvent.isCancelled()){
					return;
				}
				senderName = banEvent.getSenderName();
				reason = banEvent.getReason();
				action_id = banEvent.getActionID();
				duration = banEvent.getDuration();
				measure = banEvent.getMeasure();
			}
			ProxiedPlayer targetTmp;
			if(! playerUUID.equals("")){
				targetTmp = plugin.getProxy().getPlayer(playerUUID);
			}else{
				targetTmp = plugin.getProxy().getPlayer(playerName);
			}
			if(targetTmp != null && action_id != 3){
				if(Perms.EXEMPT_BAN.has(targetTmp)){
					Util.message(senderName, localize("banExemptPlayer", I18n.PLAYER, targetTmp.getName()));
					return;
				}
			}else if(playerName != null && action_id != 3){
				if(Perms.EXEMPT_BAN.has(playerName)){
					Util.message(senderName, localize("banExemptPlayer", I18n.PLAYER, playerName));
					return;
				}
			}
			switch(action_id){
				case 0:
					globalBan();
					break;
				case 1:
					localBan();
					break;
				case 2:
					tempBan();
					break;
				case 3:
					unBan();
					break;
			}
		}else{
			log.warn("Error: MCBans caught an invalid action. Perhaps another plugin is using MCBans improperly?");
		}
	}

	private void unBan(){
		PlayerUnbanEvent unbanEvent = new PlayerUnbanEvent(playerName, playerUUID, senderName, senderUUID);
		plugin.getProxy().getPluginManager().callEvent(unbanEvent);
		if(unbanEvent.isCancelled()){
			return;
		}
		senderName = unbanEvent.getSenderName();

		bungeeBan(false);

		JsonHandler webHandle = new JsonHandler(plugin);
		HashMap<String, String> url_items = new HashMap<>();
		url_items.put("player", playerName);
		url_items.put("player_uuid", playerUUID);
		url_items.put("admin", senderName);
		url_items.put("admin_uuid", senderUUID);
		url_items.put("exec", "unBan");
		HashMap<String, String> response = webHandle.mainRequest(url_items);

		if(response.containsKey("error")){
			Util.message(senderName, ChatColor.RED + "Error: " + response.get("error"));
			return;
		}
		if(! response.containsKey("result")){
			Util.message(senderName, ChatColor.RED + localize("unBanError", I18n.PLAYER, playerName, I18n.SENDER, senderName));
			return;
		}
		switch(response.get("result")){
			case "y":
				if(response.containsKey("player")){
					playerName = response.get("player");
				}
				// TODO Check what is this...?
				// if(!Util.isValidIP(playerName)){
				// }

				Util.broadcastMessage(ChatColor.GREEN + localize("unBanSuccess", I18n.PLAYER, playerName, I18n.SENDER, senderName));
				plugin.getProxy().getPluginManager().callEvent(new PlayerUnbannedEvent(playerName, playerUUID, senderName, senderUUID));

				log.info(senderName + " unbanned " + playerName + "!");
				return;
			case "e":
				Util.message(senderName, ChatColor.RED + localize("unBanError", I18n.PLAYER, playerName, I18n.SENDER, senderName));
				break;
			case "s":
				Util.message(senderName, ChatColor.RED + localize("unBanGroup", I18n.PLAYER, playerName, I18n.SENDER, senderName));
				break;
			case "n":
				Util.message(senderName, ChatColor.RED + localize("unBanNot", I18n.PLAYER, playerName, I18n.SENDER, senderName));
				break;
		}
		log.info(senderName + " tried to unban " + playerName + "!");
	}

	private void localBan(){
		PlayerLocalBanEvent localBanEvent = new PlayerLocalBanEvent(playerName, playerUUID, playerIP, senderName, senderUUID, reason);
		plugin.getProxy().getPluginManager().callEvent(localBanEvent);
		if(localBanEvent.isCancelled()){
			return;
		}
		senderName = localBanEvent.getSenderName();

		bungeeBan(true);

		JsonHandler webHandle = new JsonHandler(plugin);
		HashMap<String, String> url_items = new HashMap<>();
		url_items.put("player", playerName);
		url_items.put("player_uuid", playerUUID);
		url_items.put("playerip", playerIP);
		url_items.put("reason", reason);
		url_items.put("admin", senderName);
		url_items.put("admin_uuid", senderUUID);
		if(actionData != null){
			url_items.put("actionData", actionData.toString());
		}
		url_items.put("exec", "localBan");
		HashMap<String, String> response = webHandle.mainRequest(url_items);
		try{
			if(response.containsKey("error")){
				Util.message(senderName, ChatColor.RED + "Error: " + response.get("error"));
				return;
			}
			if(response.containsKey("player")){
				playerName = response.get("player");
			}
			if(! response.containsKey("result")){
				Util.message(senderName, ChatColor.RED + " MCBans API is down or unreachable.");
				return;
			}
			switch(response.get("result")){
				case "y":
					kickPlayer(playerName, playerUUID, localize("localBanPlayer", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					Util.broadcastMessage(ChatColor.GREEN + localize("localBanSuccess", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					plugin.getProxy().getPluginManager().callEvent(new PlayerBannedEvent(playerName, playerIP, senderName, reason, action_id, duration, measure));

					log.info(playerName + " has been banned with a local type ban [" + reason + "] [" + senderName + "]!");
					return;
				case "e":
					Util.message(senderName,
							ChatColor.RED + localize("localBanError", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					break;
				case "s":
					Util.message(senderName,
							ChatColor.RED + localize("localBanGroup", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					break;
				case "a":
					Util.message(senderName,
							ChatColor.RED + localize("localBanAlready", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					break;
			}
			log.info(senderName + " tried to ban " + playerName + " with a local type ban [" + reason + "]!");
		}catch(Exception ex){
			Util.message(senderName, ChatColor.RED + " MCBans API is down or unreachable.");
			log.warn("Error occurred with local banning. Please report this to an MCBans developer.");
			ex.printStackTrace();
		}
	}

	private void globalBan(){
		PlayerGlobalBanEvent globalBanEvent = new PlayerGlobalBanEvent(playerName, playerUUID, playerIP, senderName, senderUUID, reason);
		plugin.getProxy().getPluginManager().callEvent(globalBanEvent);
		if(globalBanEvent.isCancelled()){
			return;
		}
		senderName = globalBanEvent.getSenderName();
		reason = globalBanEvent.getReason();

		bungeeBan(true);

		JsonHandler webHandle = new JsonHandler(plugin);
		HashMap<String, String> url_items = new HashMap<>();
		url_items.put("player", playerName);
		url_items.put("player_uuid", playerUUID);
		url_items.put("playerip", playerIP);
		url_items.put("reason", reason);
		url_items.put("admin", senderName);
		url_items.put("admin_uuid", senderUUID);
		if(actionData.length() > 0){
			url_items.put("actionData", actionData.toString());
		}
		url_items.put("exec", "globalBan");

		HashMap<String, String> response = webHandle.mainRequest(url_items);
		try{
			if(response.containsKey("error")){
				Util.message(senderName, ChatColor.RED + "Error: " + response.get("error"));
				return;
			}
			if(response.containsKey("player")){
				playerName = response.get("player");
			}
			if(! response.containsKey("result")){
				Util.message(senderName, ChatColor.RED + " MCBans API is down or unreachable. We added a default ban for you. To unban, use /pardon.");
				return;
			}
			switch(response.get("result")){
				case "y":
					kickPlayer(playerName, playerUUID, localize("globalBanPlayer", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					Util.broadcastMessage(ChatColor.GREEN + localize("globalBanSuccess", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					plugin.getProxy().getPluginManager().callEvent(new PlayerBannedEvent(playerName, playerIP, senderName, reason, action_id, duration, measure));

					log.info(playerName + " has been banned with a global type ban [" + reason + "] [" + senderName + "]!");
					return;
				case "e":
					Util.message(senderName,
							ChatColor.RED + localize("globalBanError", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					break;
				case "w":
					badword = response.get("word");
					Util.message(senderName,
							ChatColor.RED + localize("globalBanWarning", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP, I18n.BADWORD, badword));
					break;
				case "s":
					Util.message(senderName,
							ChatColor.RED + localize("globalBanGroup", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					break;
				case "a":
					Util.message(senderName,
							ChatColor.RED + localize("globalBanAlready", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					break;
			}
			log.info(senderName + " tried to ban " + playerName + " with a global type ban [" + reason + "]!");
		}catch(Exception ex){
			Util.message(senderName, ChatColor.RED + " MCBans API is down or unreachable. We added a default ban for you. To unban, use /pardon.");

			log.warn("Error occurred with global banning. Please report this to an MCBans developer.");
			ex.printStackTrace();
		}
	}

	private void tempBan(){
		PlayerTempBanEvent tempBanEvent = new PlayerTempBanEvent(playerName, playerUUID, playerIP, senderName, senderUUID, reason, duration, measure);
		plugin.getProxy().getPluginManager().callEvent(tempBanEvent);
		if(tempBanEvent.isCancelled()){
			return;
		}
		senderName = tempBanEvent.getSenderName();
		reason = tempBanEvent.getReason();
		duration = tempBanEvent.getDuration();
		measure = tempBanEvent.getMeasure();

		JsonHandler webHandler = new JsonHandler(plugin);
		HashMap<String, String> url_items = new HashMap<>();
		url_items.put("player", playerName);
		url_items.put("player_uuid", playerUUID);
		url_items.put("playerip", playerIP);
		url_items.put("reason", reason);
		url_items.put("admin", senderName);
		url_items.put("admin_uuid", senderUUID);
		url_items.put("duration", duration);
		url_items.put("measure", measure);
		if(actionData != null){
			url_items.put("actionData", actionData.toString());
		}
		url_items.put("exec", "tempBan");
		HashMap<String, String> response = webHandler.mainRequest(url_items);
		try{
			if(response.containsKey("error")){
				Util.message(senderName, ChatColor.RED + "Error: " + response.get("error"));
				return;
			}
			if(response.containsKey("player")){
				playerName = response.get("player");
			}
			if(! response.containsKey("result")){
				Util.message(senderName, ChatColor.RED + " MCBans API is down or unreachable. We added a default ban for you. To unban, use /pardon.");
				return;
			}
			switch(response.get("result")){
				case "y":
					kickPlayer(playerName, playerUUID, localize("tempBanPlayer", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					Util.broadcastMessage(ChatColor.GREEN + localize("tempBanSuccess", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					plugin.getProxy().getPluginManager().callEvent(new PlayerBannedEvent(playerName, playerIP, senderName, reason, action_id, duration, measure));

					log.info(playerName + " has been banned with a temp type ban [" + reason + "] [" + senderName + "]!");
					return;
				case "e":
					Util.message(senderName,
							ChatColor.RED + localize("tempBanError", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					break;
				case "s":
					Util.message(senderName,
							ChatColor.RED + localize("tempBanGroup", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					break;
				case "a":
					Util.message(senderName,
							ChatColor.RED + localize("tempBanAlready", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					break;
				case "n":
					if(response.get("msg") != null){
						Util.message(senderName, ChatColor.RED + response.get("msg"));
					}else{
						Util.message(senderName,
								ChatColor.RED + localize("tempBanError", I18n.PLAYER, playerName, I18n.SENDER, senderName, I18n.REASON, reason, I18n.IP, playerIP));
					}
					break;
			}

			log.info(senderName + " tried to ban " + playerName + " with a temp type ban [" + reason + "]!");
		}catch(Exception ex){
			log.warn("Error occurred with temporary banning. Please report this to an MCBans developer.");
			ex.printStackTrace();
		}
	}

	private void bungeeBan(final boolean flag){
		if(flag){
			if(! plugin.getBanManager().isBanned(playerName)){
				plugin.getBanManager().ban(playerName);
			}
		}else{
			if(plugin.getBanManager().isBanned(playerName)){
				plugin.getBanManager().pardon(playerName);
			}
		}
	}

}
