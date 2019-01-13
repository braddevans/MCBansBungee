package com.mcbans.rona_tombo.mcbansbungee;

import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;

public enum BanType{
	GLOBAL("globalBan", Perms.BAN_GLOBAL),
	LOCAL("localBan", Perms.BAN_LOCAL),
	TEMP("tempBan", Perms.BAN_TEMP),

	UNBAN("unBan", Perms.UNBAN);

	final String actionName;
	final Perms permission;

	BanType(final String actionName, final Perms permission){
		this.actionName = actionName;
		this.permission = permission;
	}

	public Perms getPermission(){
		return permission;
	}

	public String getActionName(){
		return actionName;
	}
}
