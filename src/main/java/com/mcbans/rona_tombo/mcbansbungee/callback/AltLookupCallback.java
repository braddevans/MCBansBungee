package com.mcbans.rona_tombo.mcbansbungee.callback;

import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.api.data.AltLookupData;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class AltLookupCallback extends BaseCallback{
	public AltLookupCallback(final MCBansBungee plugin, final CommandSender sender){
		super(plugin, sender);
	}

	public AltLookupCallback(){
		super(MCBansBungee.getInstance(), null);
	}

	public void success(final AltLookupData data){
		Util.message(sender, Util.color("&7Player &c" + data.getPlayerName() + "&7 may have &f" + data.getAltCount() + "&7 alternate account(s)."));
		if(data.getAltCount() > 0){
			StringBuilder builder = new StringBuilder();

			HashMap<String, Double> map = data.getAltMap();
			boolean first = true;
			for(Map.Entry<String, Double> entry : map.entrySet()){
				String alt = entry.getKey();
				double rep = entry.getValue();
				String repStr = ((rep > 10) ? "&c" : "&9") + rep;

				if(first){
					first = false;
				}else{
					builder.append("&8 / ");
				}

				builder.append("&b")
						.append(alt)
						.append("&8 (")
						.append(repStr)
						.append("&8)");
			}

			Util.message(sender, Util.color(builder.toString()));
		}
	}

	@Override
	public void success(){
		throw new IllegalArgumentException("Wrong usage!");
	}

	@Override
	public void error(final String error){
		if(error != null && sender != null){
			Util.message(sender, ChatColor.RED + error);
		}
	}
}
