package com.mcbans.rona_tombo.mcbansbungee;

import com.mcbans.rona_tombo.mcbansbungee.ban.BungeeLocalBan;
import com.mcbans.rona_tombo.mcbansbungee.callback.BanSync;
import com.mcbans.rona_tombo.mcbansbungee.callback.MainCallBack;
import com.mcbans.rona_tombo.mcbansbungee.callback.ServerChoose;
import com.mcbans.rona_tombo.mcbansbungee.command.MCBansCommandHandler;
import com.mcbans.rona_tombo.mcbansbungee.listener.PlayerListener;
import com.mcbans.rona_tombo.mcbansbungee.permission.Perms;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public final class MCBansBungee extends Plugin{
	public static boolean AnnounceAll = false;
	private static MCBansBungee instance;
	public final String apiRequestSuffix = "4.4.3";
	public HashMap<String, Integer> connectionData = new HashMap<>();
	public HashMap<String, HashMap<String, String>> playerCache = new HashMap<>();
	public HashMap<String, Long> resetTime = new HashMap<>();
	public Properties lastSyncs = new Properties();
	public ArrayList<String> mcbStaff = new ArrayList<>();
	public long last_req = 0;
	public long timeReceived = 0;
	public Thread callbackThread = null;
	public BanSync banSync = null;
	public Thread syncBan = null;
	public long lastID = 0;
	public long lastSync = 0;
	public File syncIni = null;
	public String lastType = "";
	public boolean syncRunning = false;
	public long lastCallBack = 0;
	public String apiServer = null;
	private ActionLog log;
	private ConfigurationManager config;
	private BungeeLocalBan banManager;

	public static MCBansBungee getInstance(){
		return instance;
	}

	public static String getPrefix(){
		return instance.config.getPrefix();
	}

	@Override
	public void onEnable(){
		// Plugin startup logic
		instance = this;
		PluginManager pluginManager = getProxy().getPluginManager();
		config = new ConfigurationManager(this);
		log = new ActionLog(this);
		banManager = new BungeeLocalBan(this);
		banManager.setup();
		syncIni = new File(getDataFolder(), "sync.ini");
		if(syncIni.exists()){
			try{
				lastSyncs.load(new FileInputStream(syncIni));
				lastID = Long.valueOf(lastSyncs.getProperty("lastId"));
				lastType = lastSyncs.getProperty("lastType");
			}catch(Exception ignore){
			}
		}else{
			lastType = "bans";
			lastID = 0;
		}

		config = new ConfigurationManager(this);
		try{
			config.loadConfig(true);
		}catch(Exception ex){
			log.warn("An error occurred while trying to load the config.yml.");
			ex.printStackTrace();
		}
		log.info("Loading language file: " + config.getLanguage());
		I18n.init(config.getLanguage());

		pluginManager.registerListener(this, new PlayerListener(this));

		Perms.setupPermissionHandler();

		registerCommands();

		MainCallBack thisThread = new MainCallBack(this);
		callbackThread = new Thread(thisThread);
		callbackThread.start();

		banSync = new BanSync(this);
		syncBan = new Thread(banSync);
		syncBan.start();

		ServerChoose serverChooser = new ServerChoose(this);
		(new Thread(serverChooser)).start();
	}

	@Override
	public void onDisable(){
		// Plugin shutdown logic
		if(callbackThread != null){
			if(callbackThread.isAlive()){
				callbackThread.interrupt();
			}
		}
		if(syncBan != null){
			if(syncBan.isAlive()){
				syncBan.interrupt();
			}
		}

		getProxy().getScheduler().cancel(this);
		instance = null;

		PluginDescription des = getDescription();
		log.info(des.getName() + " version " + des.getVersion() + " is disables!");
	}

	public ActionLog getLog(){
		return log;
	}

	public ConfigurationManager getConfigs(){
		return config;
	}

	public void debug(final String message){
		if(getConfigs().isDebug()){
			getLog().info(message);
		}
	}

	public BungeeLocalBan getBanManager(){
		return banManager;
	}

	private void registerCommands(){
		addCommand("kick");
		addCommand("ban");
		addCommand("globalban");
		addCommand("gban");
		addCommand("rban");
		addCommand("namelookup");
		addCommand("nlup");
		addCommand("unban");
		addCommand("tempban");
		addCommand("tban");
		addCommand("banip");
		addCommand("ipban");
		addCommand("lookup");
		addCommand("lup");
		addCommand("banlookup");
		addCommand("blup");
		addCommand("altlookup");
		addCommand("alup");
		addCommand("alt");
		addCommand("mcbans");
		addCommand("mcbs");
	}

	private void addCommand(String cmd){
		PluginManager manager = getProxy().getPluginManager();
		manager.registerCommand(this, new MCBansCommandHandler(cmd));
	}
}
