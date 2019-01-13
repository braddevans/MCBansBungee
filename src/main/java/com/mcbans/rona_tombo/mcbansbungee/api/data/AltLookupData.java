package com.mcbans.rona_tombo.mcbansbungee.api.data;

import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AltLookupData{
	private HashMap<String, Double> altMap = new HashMap<>();
	private String playerName;
	private int altCount;

	public AltLookupData(final String playerName, final JSONObject response) throws JSONException, NullPointerException{
		if(playerName == null || response == null){
			return;
		}

		this.playerName = playerName;

		if(Util.isInteger(response.getString("altListCount"))){
			this.altCount = Integer.parseInt(response.getString("altListCount"));
		}

		String[] altList = response.getString("altList").split(",");
		String[] repList = response.getString("repList").split(",");

		if(altList.length != repList.length){
			return;
		}

		altMap.clear();
		for(int i = 0; i < altList.length; i++){
			String repStr = repList[i].trim();
			if(! Util.isDouble(repStr)){
				continue;
			}
			altMap.put(altList[i].trim(), Double.parseDouble(repStr));
		}
	}

	public String getPlayerName(){
		return playerName;
	}

	public int getAltCount(){
		return altCount;
	}

	public HashMap<String, Double> getAltMap(){
		return altMap;
	}
}
