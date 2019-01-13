package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.callback.BaseCallback;
import org.json.JSONObject;

import java.util.HashMap;

public abstract class BaseRequest<CallBack extends BaseCallback> implements Runnable{
	protected final MCBansBungee plugin;
	protected final ActionLog log;

	protected HashMap<String, String> items;
	protected CallBack callback;

	BaseRequest(MCBansBungee plugin, final CallBack callback){
		this.plugin = plugin;
		this.log = plugin.getLog();
		this.callback = callback;

		this.items = new HashMap<>();
	}

	@Override
	public void run(){
		if(! checkServer()){
			callback.error("&cCould not select or detect the MCBans API server.");
			return;
		}
		execute();
	}

	protected abstract void execute();

	private boolean checkServer(){
		while(plugin.apiServer == null){
			try{
				Thread.sleep(1000);
			}catch(InterruptedException ignore){
			}
		}

		return plugin.apiServer != null;
	}

	protected void request(){
		JsonHandler webHandle = new JsonHandler(plugin);
		webHandle.mainRequest(items);
	}

	String request_String(){
		JsonHandler webHandle = new JsonHandler(plugin);
		String urlReq = webHandle.urlParse(items);
		return webHandle.request_from_api(urlReq);

	}

	JSONObject request_JOBJ(){
		JsonHandler webHandle = new JsonHandler(plugin);
		return webHandle.hdl_jobj(items);
	}
}
