package com.mcbans.rona_tombo.mcbansbungee.callback;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.request.JsonHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerChoose implements Runnable{
	private final MCBansBungee plugin;
	private final ActionLog log;

	private final List<String> apiServers = new ArrayList<String>(4){{
		add("api.mcbans.com");
	}};

	public ServerChoose(MCBansBungee plugin){
		this.plugin = plugin;
		this.log = plugin.getLog();
	}

	@Override
	public void run(){
		plugin.apiServer = null;
		log.info("Connecting to the API server.");

		long d = 99999;
		String fastest = null;
		for(String server : apiServers){
			try{
				long pingTime = (System.currentTimeMillis());
				JsonHandler webHandle = new JsonHandler(plugin);
				HashMap<String, String> items = new HashMap<>();
				items.put("exec", "check");
				String urlReq = webHandle.urlParse(items);
				String jsonText = webHandle.request_from_api(urlReq, server);
				if(jsonText.equals("up")){
					long ft = ((System.currentTimeMillis()) - pingTime);
					log.info("API Server found: " + server + " :: response time: " + ft);

					if(d > ft){
						d = ft;
						fastest = server;
					}
				}
			}catch(IllegalArgumentException | NullPointerException ignore){
			}
		}

		if(fastest != null){
			log.info("Fastest server selected: " + fastest + " :: response time: " + d);
		}else{
			log.warn("Cannot reach the MCBans API Server.");
			log.warn("Check your network connection or notify MCBans staff.");
		}
		plugin.apiServer = fastest;
	}
}

