package com.mcbans.rona_tombo.mcbansbungee.api.data;

import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlayerLookupData{
	private String name;
	private int total = 0;
	private double reputation = 10.0D;

	private List<String> global = new ArrayList<>();
	private List<String> local = new ArrayList<>();
	private List<String> other = new ArrayList<>();

	public PlayerLookupData(final String name, final JSONObject response) throws JSONException, NullPointerException{
		if(name == null || response == null){
			return;
		}

		this.name = name;
		if(response.has("player")){
			this.name = response.getString("player");
		}
		if(Util.isInteger(response.getString("total").trim())){
			total = Integer.parseInt(response.getString("total").trim());
		}

		if(Util.isDouble(response.getString("reputation").trim())){
			reputation = Double.parseDouble(response.getString("reputation").trim());
		}
		if(response.getJSONArray("global").length() > 0){
			for(int v = 0; v < response.getJSONArray("global").length(); v++){
				global.add(response.getJSONArray("global").getString(v));
			}
		}
		if(response.getJSONArray("local").length() > 0){
			for(int v = 0; v < response.getJSONArray("local").length(); v++){
				local.add(response.getJSONArray("local").getString(v));
			}
		}
		if(response.getJSONArray("other").length() > 0){
			for(int v = 0; v < response.getJSONArray("other").length(); v++){
				other.add(response.getJSONArray("other").getString(v));
			}
		}
	}

	public String getPlayerName(){
		return name;
	}

	public int getTotal(){
		return total;
	}

	public double getReputation(){
		return reputation;
	}

	public List<String> getGlobals(){
		return global;
	}

	public List<String> getLocals(){
		return local;
	}

	public List<String> getOthers(){
		return other;
	}
}
