package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.I18n;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.event.PlayerKickEvent;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

public class Kick implements Runnable{
	private final MCBansBungee plugin;

	private final String playerName;
	private final String senderName;
	private String reason, senderUUID, playerUUID;

	public Kick(final MCBansBungee plugin, final String playerName, final String playerUUID, final String senderName, final String senderUUID, final String reason){
		this.plugin = plugin;
		this.playerName = playerName;
		this.senderName = senderName;
		this.reason = reason;
		this.senderUUID = senderUUID;
		this.playerUUID = playerUUID;
	}

	@Deprecated
	public Kick(final MCBansBungee plugin, final String playerName, final String senderName, final String reason){
		this(plugin, playerName, "", senderName, "", reason);
	}

	@Override
	public void run(){
		ProxiedPlayer playertmp;
		if(! playerUUID.equals("")){
			playertmp = plugin.getProxy().getPlayer(playerUUID);
		}else{
			playertmp = plugin.getProxy().getPlayer(playerName);
		}
		final ProxiedPlayer player = playertmp;
		if(player != null){
			// Check exempt permission
			if(Perms.EXEMPT_KICK.has(player)){
				Util.message(senderName, ChatColor.RED + localize("kickExemptPlayer", I18n.PLAYER, player.getName()));
				return;
			}

			// Call PlayerKickEvent
			PlayerKickEvent kickEvent = new PlayerKickEvent(player.getName(), playerUUID, senderName, senderUUID, reason);
			plugin.getProxy().getPluginManager().callEvent(kickEvent);
			if(kickEvent.isCancelled()){
				return;
			}
			reason = kickEvent.getReason();

			// kick player
			plugin.getProxy().getScheduler().schedule(plugin, () -> player.disconnect(TextComponent.fromLegacyText(localize("kickPlayer", I18n.PLAYER, player.getName(), I18n.SENDER, senderName, I18n.REASON, reason))), 0L, TimeUnit.MILLISECONDS);

			Util.broadcastMessage(ChatColor.GREEN + localize("kickSuccess", I18n.PLAYER, player.getName(), I18n.SENDER, senderName, I18n.REASON, reason));
			plugin.getLog().info(senderName + " has kicked " + player.getName() + " [" + reason + "]");
		}else{
			Util.message(senderName, ChatColor.RED + localize("kickNoPlayer", I18n.PLAYER, playerName));
		}
	}
}
