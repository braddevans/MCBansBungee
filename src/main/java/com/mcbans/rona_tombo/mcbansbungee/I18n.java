package com.mcbans.rona_tombo.mcbansbungee;

import com.mcbans.rona_tombo.mcbansbungee.util.FileStructure;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class I18n{
	public static final String PLAYER = "%PLAYER%";
	public static final String SENDER = "%ADMIN%";
	public static final String REASON = "%REASON%";
	public static final String BANID = "%BANID%";
	public static final String ADMIN = "%ADMIN%";
	public static final String TYPE = "%TYPE%";
	public static final String PLAYERS = "%PLAYERS%";
	public static final String BADWORD = "%BADWORD%";
	public static final String ALTS = "%ALTS%";
	public static final String COUNT = "%COUNT%";
	public static final String IP = "%IP%";
	private static final String languageDirName = "languages";
	private static Configuration fallbackMessages;
	private static Configuration messages;

	static void init(final String locale){
		extractLanguageFiles(false);

		try{
			fallbackMessages = loadLanguageFile("default");
		}catch(Exception ex){
			ActionLog.getInstance().warn("MCBans could not load default (default.yml) messages file!");
		}

		// load custom language
		try{
			setCurrentLanguage(locale);
		}catch(Exception ex){
			ActionLog.getInstance().warn("MCBans could not load messages for " + locale + ": using default.yml");
			messages = fallbackMessages;
		}
	}

	public static void extractLanguageFiles(final boolean force){
		final File langDir = getLanguagesDir();
		FileStructure.createDir(langDir);

		List<String> locales = new ArrayList<>();

		locales.add("default");
		locales.add("dutch");
		locales.add("french");
		locales.add("german");
		locales.add("ja-jp");
		locales.add("norwegian");
		locales.add("portuguese");
		locales.add("spanish");
		locales.add("sv-se");

		for(String locale : locales){
			FileStructure.extractResource("/languages/" + locale + ".yml", langDir, force, true);
		}
	}

	public static void setCurrentLanguage(final String locale) throws Exception{
		messages = loadLanguageFile(locale);
	}

	private static Configuration loadLanguageFile(final String locale) throws Exception{
		final File langDir = getLanguagesDir();
		File file = new File(langDir, locale + ".yml");

		if(! file.isFile() || ! file.canRead()){
			ActionLog.getInstance().warn("Unknown language file: " + locale);
			return null;
		}

		Configuration conf = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);

		if(fallbackMessages != null && conf.getKeys().size() != fallbackMessages.getKeys().size()){
			for(String key : fallbackMessages.getKeys()){
				if(! conf.contains(key)){
					conf.set(key, fallbackMessages.get(key));
					ActionLog.getInstance().warn("Missing message key on " + locale + ".yml: " + key);
				}
			}
		}
		return conf;
	}

	public static String localize(final String key, final Object... args){
		if(messages == null){
			ActionLog.getInstance().warn("Localized messages file is NOT loaded.");
			return "!" + key + "!";
		}

		String msg = getString(messages, key);

		if(msg == null || msg.length() == 0){
			if(msg == null){
				ActionLog.getInstance().warn("Missing message key '" + key + "'");
			}
			msg = getString(fallbackMessages, key);
			if(msg == null || msg.length() == 0){
				ActionLog.getInstance().warn("Please delete language files and type '/mcbans reload' to regenerate them.");
				return "!" + key + "!";
			}
		}

		msg = Util.color(msg);
		msg = msg.replace("\\n", "\n");

		Map<String, Object> binds = buildBinds(args);
		for(String bindKey : binds.keySet()){
			if(bindKey == null){
				continue;
			}
			final Object obj = binds.get(bindKey);
			msg = msg.replace(bindKey, (obj != null) ? obj.toString() : "");
		}

		return msg;
	}

	private static Map<String, Object> buildBinds(final Object... args){
		Map<String, Object> bind = new HashMap<>(args.length / 2);
		if(args.length < 2){
			return bind;
		}
		for(int i = 0; i < args.length; i += 2){
			if((i + 2) > args.length){
				break;
			}
			bind.put(args[i].toString(), args[i + 1]);
		}
		return bind;
	}

	private static String getString(final Configuration conf, final String key){
		String s = null;
		Object o = conf.get(key);

		if(o instanceof String){
			s = o.toString();
		}else if(o instanceof List<?>){
			@SuppressWarnings("unchecked")
			List<String> l = (List<String>) o;
			s = Util.join(l, "\n");
		}
		return s;
	}

	private static File getLanguagesDir(){
		return new File(FileStructure.getPluginDir(), languageDirName);
	}
}
