package com.mcbans.rona_tombo.mcbansbungee.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.ConfigurationManager;
import com.mcbans.rona_tombo.mcbansbungee.I18n;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import com.mcbans.rona_tombo.mcbansbungee.request.DisconnectRequest;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

public class PlayerListener implements Listener{
	public static Cache<String, String> cache = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.expireAfterAccess(5, TimeUnit.MINUTES)
			.build();
	private final MCBansBungee plugin;
	private final ActionLog log;
	private final ConfigurationManager config;

	public PlayerListener(final MCBansBungee plugin){
		this.plugin = plugin;
		this.log = plugin.getLog();
		this.config = plugin.getConfigs();
	}

	@EventHandler
	public void onPostLoginEvent(final PostLoginEvent event){
		try{
			String response = cache.getIfPresent(event.getPlayer().getName().toLowerCase());
			if(response == null){
				int check = 1;
				while(plugin.apiServer == null){
					try{
						Thread.sleep(1000);
					}catch(InterruptedException ignore){
					}
					check++;
					if(check > 5){
						if(plugin.getBanManager().isBanned(event.getPlayer().getName())){
							event.getPlayer().disconnect(TextComponent.fromLegacyText("You have been banned!"));
						}
						if(plugin.getBanManager().isBanned(event.getPlayer().getAddress().getHostName())){
							event.getPlayer().disconnect(TextComponent.fromLegacyText("You have been banned!"));
						}
						if(config.isFailedSafe()){
							log.warn("Can't reach the MCBans API Server! Kicked player: " + event.getPlayer().getName());
							event.getPlayer().disconnect(TextComponent.fromLegacyText(localize("unavailable")));
						}else{
							log.warn("Can't reach the MCBans API Server! Check passed player: " + event.getPlayer().getName());
						}
						return;
					}
				}

				final String uriStr = "http://" + plugin.apiServer + "/v3/" + config.getApiKey() + "/login/"
						+ URLEncoder.encode(event.getPlayer().getName(), "UTF-8") + "/"
						+ URLEncoder.encode(event.getPlayer().getAddress().getAddress().getHostAddress(), "UTF-8") + "/"
						+ plugin.apiRequestSuffix;
				final URLConnection conn = new URL(uriStr).openConnection();

				conn.setConnectTimeout(config.getTimeoutInSec() * 1000);
				conn.setReadTimeout(config.getTimeoutInSec() * 1000);
				conn.setUseCaches(false);

				try(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
					response = reader.readLine();
				}
				if(response == null){
					if(config.isFailedSafe()){
						log.warn("Null response! Kicked player: " + event.getPlayer().getName());
						event.getPlayer().disconnect(TextComponent.fromLegacyText(localize("unavailable")));
					}else{
						log.warn("Null response! Check passed player: " + event.getPlayer().getName());
					}
					return;
				}
				cache.put(event.getPlayer().getName().toLowerCase(), response);
			}else{
				plugin.debug("Retrieved from cache.");
			}
			plugin.debug("Response: " + response);
			handleConnectionData(response, event);
		}catch(SocketTimeoutException ex){
			log.warn("Cannot connection to the MCBans API Server: timeout");
			if(config.isFailedSafe()){
				event.getPlayer().disconnect(TextComponent.fromLegacyText(localize("unavailable")));
			}
		}catch(IOException ex){
			log.warn("Cannot connect to the MCBans API Server!");
			if(config.isDebug()){
				ex.printStackTrace();
			}
			if(config.isFailedSafe()){
				event.getPlayer().disconnect(TextComponent.fromLegacyText(localize("unavailable")));
			}
		}catch(Exception ex){
			log.warn("An error occurred in PostLoginEvent. Please report this!");
			ex.printStackTrace();

			if(config.isFailedSafe()){
				log.warn("Internal exception! Kicked player: " + event.getPlayer().getName());
				event.getPlayer().disconnect(TextComponent.fromLegacyText(localize("unavailable")));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(final PostLoginEvent event){
		final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(event.getPlayer().getDisplayName());
		final HashMap<String, String> pcache = plugin.playerCache.remove(player.getName());
		if(pcache == null){
			return;
		}
		if(pcache.containsKey("b")){
			Util.message(player, ChatColor.RED + localize("bansOnRecord"));
			Perms.VIEW_BANS.message(ChatColor.RED + localize("previousBans", I18n.PLAYER, player.getName()));
			if(! Perms.HIDE_VIEW.has(player)){
				String prev = pcache.get("b");
				if(config.isSendDetailPrevBans() && prev != null){
					prev = prev.trim();
					String[] bans = prev.split(",");
					for(String ban : bans){
						String[] data = ban.split("\\$");
						if(data.length == 3){
							Perms.VIEW_BANS.message(ChatColor.WHITE + data[1] + ChatColor.DARK_GRAY + " // " + ChatColor.WHITE + data[0] + ChatColor.GRAY + " (by " + data[2] + ")");
						}
					}
				}
			}
		}
		if(pcache.containsKey("d")){
			Util.message(player, ChatColor.RED + localize("disputes", I18n.COUNT, pcache.get("d")));
		}
		if(pcache.containsKey("pn")){
			StringBuilder plist = new StringBuilder();
			for(String name : pcache.get("pn").split(",")){
				plist.append(plist.length() == 0 ? "" : ", ").append(name);
			}
			Perms.VIEW_PREVIOUS.message(ChatColor.RED + localize("previousNames", I18n.PLAYER, player.getName(), I18n.PLAYERS, plist.toString()));
		}
		if(pcache.containsKey("dnsbl")){
			StringBuilder proxList = new StringBuilder();
			for(String name : pcache.get("dnsbl").split(",")){
				String from = name.split("$")[0];
				String reason = name.split("$")[1];
				proxList.append(proxList.length() == 0 ? "" : ", ").append("[ ").append(from).append(" { ").append(reason).append(" } ]");
			}
			Perms.VIEW_PROXY.message(ChatColor.RED + localize("proxyDetected", I18n.PLAYER, player.getName(), I18n.REASON, proxList.toString()));
		}
		if(pcache.containsKey("a")){
			if(! Perms.HIDE_VIEW.has(player)){
				Perms.VIEW_ALTS.message(ChatColor.DARK_PURPLE + localize("altAccounts", I18n.PLAYER, player.getName(), I18n.ALTS, pcache.get("al")));
			}
		}
		if(pcache.containsKey("m")){
			Util.message(plugin.getProxy().getConsole(), ChatColor.AQUA + player.getName() + "is an MCBans staff member.");

			plugin.getProxy().getScheduler().schedule(plugin, () -> {
				Set<ProxiedPlayer> players = Perms.VIEW_STAFF.getPlayers();
				players.addAll(Perms.ADMIN.getPlayers());
				players.addAll(Perms.BAN_GLOBAL.getPlayers());
				for(final ProxiedPlayer p : players){
					Util.message(p, ChatColor.AQUA + localize("isMCBansMod" + I18n.PLAYER, player.getName()));
				}
			}, 1L, TimeUnit.MILLISECONDS);

			Set<String> admins = new HashSet<>();
			for(ProxiedPlayer p : Perms.ADMIN.getPlayers()){
				admins.add(p.getName());
			}
			Util.message(player, ChatColor.AQUA + "You are an MCBans staff member. (BungeeCord Edition ver. " + plugin.getDescription().getVersion() + ")");
			Util.message(player, ChatColor.AQUA + "Online Admins: " + (admins.size() > 0 ? Util.join(admins, ", ") : ChatColor.GRAY + "(none)"));

			plugin.mcbStaff.add(player.getName());
		}
		if(config.isSendJoinMessage()){
			Util.message(player, ChatColor.RED + "This server is secured by MCBans.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDisconnect(PlayerDisconnectEvent event){
		(new Thread(new DisconnectRequest(plugin, event.getPlayer().getName()))).start();

		plugin.mcbStaff.remove(event.getPlayer().getName());
	}

	/*
	private class HandleConnectionData implements Runnable{
		private PostLoginEvent event;
		public HandleConnectionData(PostLoginEvent event){
			this.event = event;
		}

		@Override
		public void run(){
			String response = "";

			try{
				int check = 1;
				while(plugin.apiServer == null){
					try{
						Thread.sleep(1000);
					}catch(InterruptedException ignore){
					}
					check++;
					if(check > 5){
						if(config.isFailedSafe()){
							log.warn("Can't reach the MCBans API Server! Kicked player: " + event.getPlayer().getName());
						}else{
							log.warn("Can't reach the MCBans API Server! Check passed player: " + event.getPlayer().getName());
						}
						return;
					}
				}

				// get player information
				final String uriStr = "http://" + plugin.apiServer + "/v3/" + config.getApiKey() + "/details/"
						+ URLEncoder.encode(event.getPlayer().getName(), "UTF-8") + "/"
						+ URLEncoder.encode(event.getPlayer().getAddress().getAddress().getHostAddress(), "UTF-8") + "/"
						+ plugin.apiRequestSuffix;
				final URLConnection conn = new URL(uriStr).openConnection();

				conn.setConnectTimeout(config.getTimeoutInSec() * 1000);
				conn.setReadTimeout(config.getTimeoutInSec() * 1000);
				conn.setUseCaches(false);

				try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
					response = br.readLine();
				}

				plugin.debug("Response: " + response);

			}catch(SocketTimeoutException ex){
				log.warn("Can't connect to the MCBans API Server: timeout");
			}catch(IOException ex){
				log.warn("Can't connect to the MCBans API Server!");
				if(config.isDebug()){
					ex.printStackTrace();
				}
			}catch(Exception ex){
				log.warn("An error occurred in AsyncPlayerPreLoginEvent. Please report this to an MCBans developer.");
				ex.printStackTrace();
			}

			String[] s = response.split(";");
			if (s.length >= 6) {
				// put data to playerCache
				HashMap<String, String> pcache = new HashMap<>();
				if(s[0].equals("b")){
					if(s.length>=8){
						pcache.put("b", s[7]);
					}
				}
				if(Integer.parseInt(s[3]) > 0){
					if(s.length>=7){
						pcache.put("a", s[3]);
						pcache.put("al", s[6]);
					}
				}
				if(s[4].equals("y")){
					pcache.put("m", "y");
				}
				if(Integer.parseInt(s[5]) > 0){
					pcache.put("d", s[5]);
				}
				if(s.length>=9){
					if(!s[8].equals("")){
						pcache.put("pn", s[8]);
					}
				}
				if(s.length>=10){
					if(!s[9].equals("")){
						pcache.put("dnsbl", s[9]);
					}
				}
				final ProxiedPlayer player = event.getPlayer();
				if(pcache.containsKey("b")){
					Util.message(player, ChatColor.RED + localize("bansOnRecord"));
					Perms.VIEW_BANS.message(ChatColor.RED + localize("previousBans", I18n.PLAYER, player.getName()));
					if (!Perms.HIDE_VIEW.has(player)){
						String prev = pcache.get("b");
						if (config.isSendDetailPrevBans() && prev != null){
							prev = prev.trim();
							String[] bans = prev.split(",");
							for (String ban : bans){
								String[] data = ban.split("\\$");
								if (data.length == 3){
									Perms.VIEW_BANS.message(ChatColor.WHITE+ data[1] + ChatColor.DARK_GRAY + " // " + ChatColor.WHITE + data[0] + ChatColor.GRAY +  " (by " + data[2] + ")");
								}
							}
						}
					}
				}
				if(pcache.containsKey("d")){
					Util.message(player, ChatColor.RED + localize("disputes", I18n.COUNT, pcache.get("d")));
				}
				if(pcache.containsKey("pn")){
					StringBuilder plist = new StringBuilder();
					for(String name: pcache.get("pn").split(",")){
						plist.append(plist.length() == 0 ? "" : ", ").append(name);
					}
					Perms.VIEW_PREVIOUS.message(ChatColor.RED + localize("previousNames", I18n.PLAYER, player.getName(), I18n.PLAYERS, plist.toString() ));
				}
				if(pcache.containsKey("dnsbl")){
					StringBuilder proxlist = new StringBuilder();
					for(String name: pcache.get("dnsbl").split(",")){
						String from = name.split("$")[0];
						String reason = name.split("$")[1];
						proxlist.append(proxlist.length() == 0 ? "" : ", ").append("[ ").append(from).append(" { ").append(reason).append(" } ]");
					}
					Perms.VIEW_PROXY.message(ChatColor.RED + localize("proxyDetected", I18n.PLAYER, player.getName(), I18n.REASON, proxlist.toString() ));
				}
				if(pcache.containsKey("a")){
					if (!Perms.HIDE_VIEW.has(player))
						Perms.VIEW_ALTS.message(ChatColor.DARK_PURPLE + localize("altAccounts", I18n.PLAYER, player.getName(), I18n.ALTS, pcache.get("al")));
				}
				if(pcache.containsKey("m")){
					//Util.broadcastMessage(ChatColor.AQUA + _("isMCBansMod", I18n.PLAYER, player.getName()));
					// notify to console, mcbans.view.staff, mcbans.admin, mcbans.ban.global players
					Util.message(plugin.getProxy().getConsole(), ChatColor.AQUA + player.getName() + " is an MCBans staff member.");

					plugin.getProxy().getScheduler().schedule(plugin, () -> {
						Set<ProxiedPlayer> players = Perms.VIEW_STAFF.getPlayers();
						players.addAll(Perms.ADMIN.getPlayers());
						players.addAll(Perms.BAN_GLOBAL.getPlayers());
						for (final ProxiedPlayer p : players){
							Util.message(p, ChatColor.AQUA + localize("isMCBansMod", I18n.PLAYER, player.getName()));
						}
					}, 1L, TimeUnit.MILLISECONDS);

					// send information to mcbans staff
					Set<String> admins = new HashSet<>();
					for (ProxiedPlayer p : Perms.ADMIN.getPlayers()){
						admins.add(p.getName());
					}
					Util.message(player, ChatColor.AQUA + "You are an MCBans staff member. (ver " + plugin.getDescription().getVersion() + ")");
					Util.message(player, ChatColor.AQUA + "Online Admins: " + ((admins.size() > 0) ? Util.join(admins, ", ") : ChatColor.GRAY + "(none)"));

					// add online mcbans staff list array
					plugin.mcbStaff.add(player.getName());
				}
				if (config.isSendJoinMessage()){
					Util.message(player, ChatColor.RED + "This server is secured by MCBans.");
				}
			}else{
				if (response.contains("Server Disabled")) {
					Util.message(plugin.getProxy().getConsole(), ChatColor.RED + "This server has been disabled by MCBans staff. Please go to forums.mcbans.com.");
					return;
				}
				log.warn("Response: " + response);
			}
		}
	}
	*/

	private void handleConnectionData(String response, PostLoginEvent event){
		String[] s = response.split(";");
		if(s.length >= 5){
			// check banned
			if(s[0].equals("l") || s[0].equals("g") || s[0].equals("t") || s[0].equals("i") || s[0].equals("s")){
				String[] reasonData = s[1].split("\\$");
				event.getPlayer().disconnect(TextComponent.fromLegacyText(localize("banReturnMessage", I18n.REASON, reasonData[0], I18n.ADMIN, reasonData[1], I18n.BANID, reasonData[2], I18n.TYPE, reasonData[3])));
				return;
			}
			// check reputation
			else if(config.getMinRep() > Double.valueOf(s[2])){
				event.getPlayer().disconnect(TextComponent.fromLegacyText(localize("underMinRep")));
				return;
			}
			// check alternate accounts
			else if(config.isEnableMaxAlts() && config.getMaxAlts() < Integer.valueOf(s[3]) && ! Perms.EXEMPT_MAXALTS.has(event.getPlayer().getName())){
				event.getPlayer().disconnect(TextComponent.fromLegacyText(localize("overMaxAlts")));
				return;
			}
			// check passed, put data to playerCache
			else{
				HashMap<String, String> tmp = new HashMap<>();
				if(s[0].equals("b")){
					if(s.length >= 8){
						tmp.put("b", s[7]);
					}
				}
				if(Integer.parseInt(s[3]) > 0){
					if(s.length >= 7){
						tmp.put("a", s[3]);
						tmp.put("al", s[6]);
					}
				}
				if(s[4].equals("y")){
					tmp.put("m", "y");
				}
				if(s.length >= 6){
					if(Integer.parseInt(s[5]) > 0){
						tmp.put("d", s[5]);
					}
				}
				if(s.length >= 9){
					if(! s[8].equals("")){
						tmp.put("pn", s[8]);
					}
				}
				if(s.length >= 10){
					if(! s[9].equals("")){
						tmp.put("dnsbl", s[9]);
					}
				}
				plugin.playerCache.put(event.getPlayer().getName(), tmp);
			}
			plugin.debug(event.getPlayer().getName() + " authenticated with " + s[2] + " rep");
		}else{
			if(response.contains("Server Disabled")){
				Util.message(plugin.getProxy().getConsole(), ChatColor.RED + "This server has been disabled by MCBans staff. Please go to forums.mcbans.com.");
				return;
			}
			if(config.isFailedSafe()){
				log.warn("Invalid response!(" + s.length + ") Kicked player: " + event.getPlayer().getName());
				event.getPlayer().disconnect(TextComponent.fromLegacyText(localize("unavailable")));
			}else{
				log.warn("Invalid response!(" + s.length + ") Check passed player: " + event.getPlayer().getName());
			}
			log.warn("Response: " + response);
		}
	}
}