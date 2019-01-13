package com.mcbans.rona_tombo.mcbansbungee.command;

public enum CommandType{
	KICK(new CommandKick()),
	BAN(new CommandBan()),
	GLOBALBAN(new CommandGlobanban()),
	RBAN(new CommandRban()),
	NAMELOOKUP(new CommandPrevious()),
	UNBAN(new CommandUnban()),
	TEMPBAN(new CommandTempban()),
	BANIP(new CommandBanip()),
	LOOKUP(new CommandBanlookup()),
	BANLOOKUP(new CommandBanlookup()),
	ALTLOOKUP(new CommandAltlookup()),
	MCBANS(new CommandMCBans()),
	MCBS(new CommandMCBansSettings());

	private final Object commandClass;

	CommandType(Object commandClass){
		this.commandClass = commandClass;
	}

	public static CommandType getTypeFromCommand(String cmd){
		switch(cmd.toLowerCase()){
			case "kick":
				return KICK;
			case "ban":
				return BAN;
			case "globalban":
			case "gban":
				return GLOBALBAN;
			case "rban":
				return RBAN;
			case "namelookup":
			case "nlup":
				return NAMELOOKUP;
			case "unban":
				return UNBAN;
			case "tempban":
			case "tban":
				return TEMPBAN;
			case "banip":
			case "ipban":
				return BANIP;
			case "lookup":
			case "lup":
				return LOOKUP;
			case "banlookup":
			case "blup":
				return BANLOOKUP;
			case "altlookup":
			case "alup":
			case "alt":
				return ALTLOOKUP;
			case "mcbans":
				return MCBANS;
			case "mcbs":
				return MCBS;
			default:
				return null;
		}
	}

	public Object getCommandClass(){
		return commandClass;
	}
}
