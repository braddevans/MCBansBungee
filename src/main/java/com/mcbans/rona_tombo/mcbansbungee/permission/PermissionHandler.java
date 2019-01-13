package com.mcbans.rona_tombo.mcbansbungee.permission;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PermissionHandler{
	private static PermissionHandler instance;

	private PermissionHandler(){
		instance = this;
	}

	public static PermissionHandler getInstance(){
		if(instance == null){
			synchronized(PermissionHandler.class){
				if(instance == null){
					instance = new PermissionHandler();
				}
			}
		}
		return instance;
	}

	public boolean has(final String playerName, final String permission){
		if(playerName == null){
			return false;
		}
		if(playerName.equals("Console")){
			return true;
		}
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
		if(player == null){
			return false;
		}else{
			return player.hasPermission(permission);
		}
	}
}
