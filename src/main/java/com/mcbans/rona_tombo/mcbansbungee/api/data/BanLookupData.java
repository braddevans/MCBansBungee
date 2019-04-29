package com.mcbans.rona_tombo.mcbansbungee.api.data;

import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import org.json.JSONException;
import org.json.JSONObject;

public class BanLookupData{
	private int banID;

	private String name;
	private String server;
	private String admin;
	private String reason;
	private String date;
	private double lostRep = 0;
	private String type;

	public BanLookupData(final int banID, final JSONObject response) throws JSONException, NullPointerException{
		if(banID < 0 || response == null){
			return;
		}
		this.banID = banID;
		this.name = response.getString("player");
		this.admin = response.getString("admin");
		this.reason = response.getString("reason");
		this.server = response.getString("server");
		this.date = response.getString("date");
		this.type = response.getString("type");

		if(Util.isDouble(response.getString("reploss"))){
			this.lostRep = Double.parseDouble(response.getString("reploss"));
		}
	}

	public int getBanID(){
		return banID;
	}

	public String getPlayerName(){
		return name;
	}

	public String getAdminName(){
		return admin;
	}

	public String getReason(){
		return reason;
	}

	public String getDate(){
		return date;
	}

	public double getLostRep(){
		return lostRep;
	}

	public String getType(){
		return type;
	}

	public String getServer(){
		return server;
	}
}
