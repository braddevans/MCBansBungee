package com.mcbans.rona_tombo.mcbansbungee;

import net.md_5.bungee.api.ProxyServer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActionLog{
	private static ActionLog instance;
	private final Logger logger = ProxyServer.getInstance().getLogger();
	private final String logPrefix = "[MCBans] ";
	private final MCBansBungee plugin;

	public ActionLog(final MCBansBungee plugin){
		instance = this;
		this.plugin = plugin;
	}

	public static ActionLog getInstance(){
		return instance;
	}

	public void log(final Level level, final String message, final boolean logToFile){
		logger.log(level, logPrefix + message);

	}

	public void log(final Level level, final String message){
		log(level, message, true);
	}

	public void fine(final String message){
		log(Level.FINE, message);
	}

	public void info(final String message){
		log(Level.INFO, message);
	}

	public void warn(final String message){
		log(Level.WARNING, message);
	}

	public void severe(final String message){
		log(Level.SEVERE, message);
	}

	private void writeLog(final String message){

	}

	private void appendLine(final String file, final String line) throws IOException{
		try(PrintWriter writer = new PrintWriter(new FileWriter(file, true))){
			writer.append(line);
		}
	}
}
