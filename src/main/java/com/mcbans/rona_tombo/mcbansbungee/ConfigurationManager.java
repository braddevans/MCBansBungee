package com.mcbans.rona_tombo.mcbansbungee;

import com.mcbans.rona_tombo.mcbansbungee.util.FileStructure;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigurationManager{
	/* Current config.yml File Version! */
	private final int latestVersion = 2;

	private final MCBansBungee plugin;
	private final ActionLog log;

	private Configuration conf;
	private File pluginDir;

	private boolean isValidKey = false;

	private ConfigurationProvider yaml = YamlConfiguration.getProvider(YamlConfiguration.class);

	/**
	 * Constructor
	 */
	ConfigurationManager(final MCBansBungee plugin){
		this.plugin = plugin;
		this.log = plugin.getLog();

		this.pluginDir = this.plugin.getDataFolder();
	}

	/**
	 * Load config.yml
	 */
	public void loadConfig(final boolean initialLoad) throws Exception{
		// create directories
		FileStructure.createDir(pluginDir);

		File file = new File(pluginDir, "config.yml");
		if(! file.exists()){
			FileStructure.extractResource("/config.yml", pluginDir, false, false);
			log.log(Level.INFO, "config.yml has not been found! We created a default config.yml for you!", false);
		}

		conf = yaml.load(file);

		checkVar(conf.getInt("ConfigVersion", 1));

		if(conf.getString("apiKey").trim().length() != 40){
			isValidKey = false;
			if(initialLoad){
				Util.message((CommandSender) null, ChatColor.RED + "=== Missing OR Invalid API Key! ===");
				log.severe("MCBans detected a missing or invalid API Key!");
				log.severe("Please copy your API key to the config.yml");
				log.severe("Don't you have an API key? Go to: http://my.mcbans.com/servers/");
			}else{
				log.severe("MCBans detected a missing or invalid API Key! Please check your config.yml");
			}
		}else{
			isValidKey = true;
		}

		if(isEnableLog()){
			if(! new File(getLogFile()).exists()){
				try{
					new File(getLogFile()).createNewFile();
				}catch(IOException ex){
					log.warn("Could not create log file! " + getLogFile());
				}
			}
		}

		if(! initialLoad && isEnableAutoSync()){
			plugin.banSync.goRequest();
		}
	}

	private void checkVar(final int ver){
		if(ver < latestVersion){
			final String destName = "oldconfig-v" + ver + ".yml";
			File config = new File(pluginDir, "config.yml");
			String srcPath = config.getPath();
			String destPath = new File(pluginDir, destName).getPath();
			try{
				FileStructure.copyTransfer(srcPath, destPath);
				log.info("Outdated config file! Automatically copied old config.yml to " + destName + "!");
			}catch(Exception ex){
				log.warn("Failed to copy old config.yml!");
			}

			FileStructure.extractResource("/config.yml", pluginDir, true, false);
			try{
				conf = yaml.load(config);
				log.info("Deleted existing configuration file and generate an new one!");
			}catch(IOException ex){
				log.warn("An error has occurred with loading config");
			}
		}
	}

	public String getPrefix(){
		return Util.color(conf.getString("prefix", "[MCBans]"));
	}

	public String getApiKey(){
		if(! isValidKey){
			Util.message((CommandSender) null, ChatColor.RED + "Invalid API Key! Edit your config.yml and type /mcbans reload");
			return "";
		}
		return conf.getString("apiKey", "").trim();
	}

	public String getLanguage(){
		return conf.getString("language", "default");
	}

	public String getDefaultLocal(){
		return conf.getString("defaultLocal", "You have been banned!");
	}

	public String getDefaultTemp(){
		return conf.getString("defaultTemp", "You have been temporarily banned!");
	}

	public String getDefaultKick(){
		return conf.getString("defaultKick", "You have been kicked!");
	}

	public boolean isDebug(){
		return conf.getBoolean("isDebug", false);
	}

	public boolean isEnableLog(){
		return conf.getBoolean("logEnable", false);
	}

	private String getLogFile(){
		return conf.getString("logFile", "plugins/MCBansBungee/actions.log");
	}

	public boolean isEnableMaxAlts(){
		return conf.getBoolean("enableMaxAlts", false);
	}

	public int getMaxAlts(){
		return conf.getInt("maxAlts", 2);
	}

	public boolean isEnableAutoSync(){
		return conf.getBoolean("enableAutoSync", true);
	}

	public int getSyncInterval(){
		return conf.getInt("autoSyncInterval", 5);
	}

	public boolean isSendJoinMessage(){
		return conf.getBoolean("onJoinMCBansMessage", false);
	}

	public boolean isSendDetailPrevBans(){
		return conf.getBoolean("sendDetailPrevBanOnJoin", false);
	}

	public double getMinRep(){
		return conf.getDouble("minRep", 3.0D);
	}

	public int getCallBackInterval(){
		return conf.getInt("callBackInterval", 15);
	}

	public int getTimeoutInSec(){
		return conf.getInt("timeout", 10);
	}

	public boolean isFailedSafe(){
		return conf.getBoolean("failsafe", false);
	}

	public boolean isValidApiKey(){
		return isValidKey;
	}
}
