package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.ConfigurationManager;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class JsonHandler{
	private final MCBansBungee plugin;
	private final ActionLog log;
	private final ConfigurationManager config;

	public JsonHandler(MCBansBungee plugin){
		this.plugin = plugin;
		this.log = plugin.getLog();
		this.config = plugin.getConfigs();
	}

	private JSONObject get_data(String json_text){
		try{
			return new JSONObject(json_text);
		}catch(JSONException ex){
			if(config.isDebug()){
				ex.printStackTrace();
			}
		}
		return null;
	}

	public HashMap<String, String> mainRequest(HashMap<String, String> items){
		HashMap<String, String> out = new HashMap<>();
		String url_req = urlParse(items);
		String json_text = request_from_api(url_req);
		if(config.isDebug()){
			log.info("Requested: '" + url_req + "'");
			log.info("Converting response: '" + json_text + "'");
		}
		if(json_text == null || json_text.length() <= 0){
			if(config.isDebug()){
				log.severe("Null Response. Please contact an MCBans developer.");
				out.clear();
				return out;
			}
		}

		JSONObject output = get_data(json_text);
		if(output != null){
			for(String s : output.keySet()){
				try{
					out.put(s, output.get(s).toString());
				}catch(JSONException ex){
					if(config.isDebug()){
						log.severe("Json Error On Retrieval!");
						ex.printStackTrace();
					}
				}
			}
		}
		return out;
	}

	public JSONObject hdl_jobj(HashMap<String, String> items){
		String urlReq = urlParse(items);
		String jsonText = request_from_api(urlReq);
		return get_data(jsonText);
	}

	String request_from_api(String data){
		return request_from_api(data, plugin.apiServer);
	}

	public String request_from_api(String data, String server){
		OutputStreamWriter writer = null;
		BufferedReader reader = null;
		try{
			if(! config.isValidApiKey()){
				return "";
			}
			if(config.isDebug()){
				log.info("Sending API request: '" + data + "'");
			}
			URL url = new URL("http://" + server + "/v3/" + config.getApiKey());
			URLConnection con = url.openConnection();
			con.setConnectTimeout(25000);
			con.setReadTimeout(25000);
			con.setDoOutput(true);

			writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(data);
			writer.flush();

			StringBuilder builder = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			while((line = reader.readLine()) != null){
				builder.append(line);
			}
			if(plugin.getConfigs().isDebug()){
				log.info("Result: " + builder.toString());
			}
			return builder.toString();
		}catch(Exception ex){
			if(config.isDebug()){
				log.severe("Error fetching data!");
				ex.printStackTrace();
			}
			return "";
		}finally{
			if(writer != null){
				try{
					writer.close();
				}catch(Exception ignore){
				}
			}
			if(reader != null){
				try{
					reader.close();
				}catch(Exception ignore){
				}
			}
		}
	}

	public String urlParse(HashMap<String, String> items){
		StringBuilder data = new StringBuilder();
		try{
			for(Map.Entry<String, String> entry : items.entrySet()){
				String key = entry.getKey();
				String val = entry.getValue();
				if(val != null && ! val.equals("")){
					if(data.toString().equals("")){
						data = new StringBuilder(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(val, "UTF-8"));
					}else{
						data.append("&").append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(val, "UTF-8"));
					}
				}
			}
		}catch(UnsupportedEncodingException ex){
			if(config.isDebug()){
				ex.printStackTrace();
			}
		}
		return data.toString();
	}
}
