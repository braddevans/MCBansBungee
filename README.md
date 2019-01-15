MCBans Server Plugin for BungeeCord
Author rona_tombo

This plugin corresponds to BungeeCord with the MCBans plugin developed by Firestarthe, Syamn, Corpdraco.
This description is almost identical to that of the MCBans plug-in.


Welcome to MCBans
====================

MCBans is a global banning solution for Minecraft, provided through the Bukkit and BungeeCord plugin. The aim of the plugin is to provide server owners with a method of assisting in the prevention of griefers on their own and other's servers, using both local and global bans.

Helpful Information
--------
Main Site: https://mcbans.com

Support Desk: https://forums.mcbans.com/support-tickets/open

Community: https://forums.mcbans.com

Expectations
--------

Please remember the following things while using our plugin!

* MCBans is maintained constantly by a team of staff, but we're not miracle workers! Like everyone, we need time to perform our tasks and can't do everything on demand.
* MCBans isn't always 100% clear from invalid bans - but, we're well on our way. Your help is always thanked in removing invalid bans and we try our hardest to monitor everything.
* MCBans has a small dedicated development team - We do only have a few dedicated developers, but they do have other commitments as well.
* MCBans staff don't always have good memories! Visit our support desk.

Commands
--------

**/rban command cannot use MCBans BungeeCord Edition**

Local Ban Variations
* /ban <playername|UUID> <reason> - bans the player

E.G. /ban Firestarthe breaking my sign

Global Ban Variations
* /ban <playername|UUID> g <reason> - global ban
* /gban <playername|UUID> <reason> - global ban

E.G. /ban g Firestarthe griefing

Temporary Ban Variations
* /ban <playername|UUID> t <int> <m/h/d> <reason> - temp bans a player
* /tban <playername|UUID> <int> <m/h/d> <reason> - temp bans a player

E.G. /tban Firestarthe 15 m you are banned for 15 minutes

IP Ban Variations
* /banip <IP> [reason] - bans an IP address

Misc Commands
* /lookup <playername|UUID> - checks players ban history (local/global bans)
* /banlookup <banID> - checks ban details
* /altlookup <playername|UUID> - checks alt accounts (premium server only!)
* /kick <playername|UUID> [reason] - kicks a player from the game
* /unban <playername|IP|UUID> - unbans the player/IP from your server
* /mcbans - mcbans help and more information
* /mcbs - change server settings

Permissions
--------

* mcbans.admin - Admin privileges to mcbans
* mcbans.ban.global - Allow global ban player
* mcbans.ban.local - Allow local ban player
* mcbans.ban.temp - Allow temp ban player
* mcbans.ban.ip - Allow use banip command
* mcbans.ban.exempt - Permission to exempt from bans
* mcbans.unban - Allow unban player
* mcbans.view.alts - Show notification of a players alts on connect
* mcbans.view.bans - Show previous ban information on player connect
* mcbans.view.staff - Show notification of a mcbans staff on connect
* mcbans.hideview - Hide player view alts/previous bans on connect
* mcbans.lookup.player - Allow lookup player ban history
* mcbans.lookup.ban - Allow lookup ban details
* mcbans.lookup.alt - Allow lookup alt account
* mcbans.kick - Allow kick player
* mcbans.kick.exempt - Permission to exempt from kicks
* mcbans.maxalts.exempt - Permission to exempt from max alt account disconnect
