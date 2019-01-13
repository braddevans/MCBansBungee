package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.ConfigurationManager;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.exception.CommandException;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

abstract public class BaseCommand{
	protected final MCBansBungee plugin;
	protected List<String> args = new ArrayList<>();
	protected String senderName = "Console", senderUUID;
	protected ProxiedPlayer player;
	protected boolean isPlayer = false;
	protected int argLength;
	protected boolean bePlayer = false;
	protected boolean banning = false;
	protected CommandSender sender;
	protected String target = "";
	protected String targetIP = "";
	protected String targetUUID = "";
	protected ConfigurationManager config;

	public BaseCommand(){
		this.plugin = MCBansBungee.getInstance();
		config = plugin.getConfigs();
	}

	abstract public void run() throws CommandException;

	abstract public boolean hasPermission(CommandSender sender);

	void init(CommandSender sender, String[] preArgs){
		this.args.clear();
		this.player = null;
		this.isPlayer = false;
		this.senderName = "Console";

		this.target = "";
		this.targetUUID = "";
		this.targetIP = "";

		this.sender = sender;

		args = new ArrayList<>(Arrays.asList(preArgs));

		if(argLength > args.size()){
			Util.message(senderName, ChatColor.RED + localize("formatError"));
			return;
		}
		if(bePlayer && ! (sender instanceof ProxiedPlayer)){
			Util.message(senderName, "&cThis command cannot be executed from the console.");
			return;
		}
		if(sender instanceof ProxiedPlayer){
			player = (ProxiedPlayer) sender;
			senderName = player.getName();
			isPlayer = true;
		}

		if(! hasPermission(sender)){
			Util.message(sender, ChatColor.RED + localize("permissionDenied"));
			return;
		}

		if(banning && args.size() > 0){
			target = args.get(0).trim();
			final ProxiedPlayer targetPlayer = plugin.getProxy().getPlayer(target);
			if(targetPlayer != null){
				InetSocketAddress address = targetPlayer.getAddress();
				if(address.isUnresolved()){
					targetIP = address.getHostString();
				}else{
					targetIP = address.getAddress().getHostAddress();
				}
			}
			if(! Util.isValidName(target)){
				if(Util.isValidUUID(target)){
					targetUUID = target;
					target = "";
				}else{
					if(Util.isValidIP(target)){
						targetIP = target;
					}else{
						Util.message(sender, ChatColor.RED + localize("invalidName"));
					}
				}
			}
		}

		try{
			run();
		}catch(CommandException ex){
			Throwable error = ex;
			while(error instanceof Exception){
				Util.message(sender, error.getMessage());
				error = error.getCause();
			}
		}
	}
}

