package com.mcbans.rona_tombo.mcbansbungee.command;

import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MCBansCommandHandler extends Command{
	protected final MCBansBungee plugin;

	private BaseCommand commandClass;

	public MCBansCommandHandler(String command){
		super(command);
		plugin = MCBansBungee.getInstance();

		commandClass = (BaseCommand) CommandType.getTypeFromCommand(command).getCommandClass();
	}

	@Override
	public void execute(final CommandSender sender, final String[] args){
		if(args.length > 0){
			args[0] = plugin.getProxy().getPlayer(args[0]) != null ? plugin.getProxy().getPlayer(args[0]).getName() : args[0];
		}
		commandClass.init(sender, args);
	}
}
