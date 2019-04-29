package com.mcbans.rona_tombo.mcbansbungee.ban;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BungeeLocalBan{
	private final MCBansBungee plugin;
	private final ActionLog log;

	private final ConfigurationProvider yaml = YamlConfiguration.getProvider(YamlConfiguration.class);
	private Configuration config;

	private List<String> banned;

	public BungeeLocalBan(final MCBansBungee plugin){
		this.plugin = plugin;
		this.log = plugin.getLog();
	}

	@SuppressWarnings("unchecked")
	public void setup(){
		File bannedFile = new File(plugin.getDataFolder(), "banned.yml");
		if(! bannedFile.exists()){
			try{
				bannedFile.getParentFile().mkdir();
				bannedFile.createNewFile();
			}catch(IOException ex){
				log.warn("banned File could not created.");
			}
		}
		try{
			config = yaml.load(bannedFile);
		}catch(IOException ex){
			log.warn("Could not load banned file.");
		}

		List<String> temp = (List<String>) config.getList("banned");
		banned = new ArrayList<>(temp);
	}

	public void ban(String playerName){
		if(playerName == null){
			System.out.println("playerName is null");
			return;
		}
		banned.add(playerName);
		config.set("banned", banned);
		try{
			yaml.save(config, new File(plugin.getDataFolder(), "banned.yml"));
		}catch(IOException ex){
			log.warn("Could not written banned file.");
		}
	}

	public void pardon(String playerName){
		banned.remove(playerName);
		config.set("banned", null);
		config.set("banned", banned);
		try{
			yaml.save(config, new File(plugin.getDataFolder(), "banned.yml"));
		}catch(IOException ex){
			log.warn("Could not written banned file.");
		}
	}

	public boolean isBanned(String playerName){
		return banned.contains(playerName);
	}
}
