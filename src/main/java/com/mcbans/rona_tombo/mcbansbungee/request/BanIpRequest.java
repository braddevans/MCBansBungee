package com.mcbans.rona_tombo.mcbansbungee.request;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.I18n;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;
import com.mcbans.rona_tombo.mcbansbungee.callback.MessageCallback;
import com.mcbans.rona_tombo.mcbansbungee.event.PlayerIPBanEvent;
import com.mcbans.rona_tombo.mcbansbungee.event.PlayerIPBannedEvent;
import com.mcbans.rona_tombo.mcbansbungee.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.mcbans.rona_tombo.mcbansbungee.I18n.localize;

public class BanIpRequest extends BaseRequest<MessageCallback>{
	private String ip;
	private String reason;
	private String issuedBy, issuedByUUID;

	public BanIpRequest(final MCBansBungee plugin, final MessageCallback callback, final String ip, final String reason, final String issuedBy, String issuedByUUID){
		super(plugin, callback);

		this.items.put("exec", "ipBan");
		this.items.put("ip", ip);
		this.items.put("reason", reason);
		this.items.put("admin", issuedBy);
		this.items.put("admin_uuid", issuedByUUID);

		this.ip = ip;
		this.reason = reason;
		this.issuedBy = issuedBy;
		this.issuedByUUID = issuedByUUID;
	}

	@Override
	protected void execute(){
		JSONObject response = request_JOBJ();

		PlayerIPBanEvent ipBanEvent = new PlayerIPBanEvent(ip, issuedBy, issuedByUUID, reason);
		plugin.getProxy().getPluginManager().callEvent(ipBanEvent);
		if(ipBanEvent.isCancelled()){
			return;
		}
		issuedBy = ipBanEvent.getSenderName();
		reason = ipBanEvent.getReason();

		if(Util.isValidIP(ip)){
			plugin.getBanManager().ban(ip);
		}

		try{
			if(response != null && response.has("result")){
				final String result = response.getString("result").trim().toLowerCase(Locale.ENGLISH);
				switch(result){
					case "y":
						callback.setBroadcastMessage(ChatColor.GREEN + localize("ipBanSuccess", I18n.IP, this.ip, I18n.SENDER, this.issuedBy, I18n.REASON, this.reason));
						callback.success();

						kickPlayerByIP(this.ip, reason);


						log.info("IP " + ip + " has been banned [" + reason + "] [" + issuedBy + "]!");
						plugin.getProxy().getPluginManager().callEvent(new PlayerIPBannedEvent(ip, issuedBy, issuedByUUID, reason));
						break;
					case "a":
						callback.error(ChatColor.RED + localize("ipBanAlready", I18n.IP, this.ip, I18n.SENDER, this.issuedBy, I18n.REASON, this.reason));
						log.info(issuedBy + " tried to IPBan " + ip + "!");
						break;
					case "n":
						callback.error(ChatColor.RED + localize("invalidIP"));
						log.info(issuedBy + " tried to IPBan " + ip + "!");
						break;
					case "e":
						callback.error(ChatColor.RED + localize("invalidIP"));
						log.info(issuedBy + " tried to IPBan " + ip + "!");
						break;
					default:
						log.severe("Invalid response result: " + result);
						break;
				}
			}else{
				callback.error(ChatColor.RED + "MCBans API appears to be down or unreachable!");
			}
		}catch(JSONException ex){
			if(response.toString().contains("error")){
				if(response.toString().contains("Server Disabled")){
					ActionLog.getInstance().severe("This server has been disabled by MCBans staff.");
					ActionLog.getInstance().severe("To appeal this decision, please file a ticket at forums.mcbans.com.");

					callback.error("This server has been disabled by MCBans staff.");
					return;
				}
				ActionLog.getInstance().severe("A JSON error occurred while trying to parse lookup data.");
				callback.error("An error occurred while parsing JSON data.");
				if(plugin.getConfigs().isDebug()){
					ex.printStackTrace();
				}
			}
		}
	}

	private void kickPlayerByIP(final String ip, final String kickReason){
		for(final ProxiedPlayer p : plugin.getProxy().getPlayers()){
			if(ip.equals(p.getAddress().getAddress().getHostAddress())){
				plugin.getProxy().getScheduler().schedule(plugin, () -> p.disconnect(TextComponent.fromLegacyText(kickReason)), 0L, TimeUnit.MILLISECONDS);
			}
		}
	}
}


