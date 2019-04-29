package com.mcbans.rona_tombo.mcbansbungee.util;

import com.mcbans.rona_tombo.mcbansbungee.ActionLog;
import com.mcbans.rona_tombo.mcbansbungee.MCBansBungee;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class FileStructure{
	public static void createDir(final File dir){
		// if already exists, do nothing
		if(dir.isDirectory()){
			return;
		}
		if(! dir.mkdir()){
			ActionLog.getInstance().warn("Cannot create directory: " + dir.getName());
		}
	}

	public static void copyTransfer(String srcPath, String destPath) throws IOException{
		try(FileChannel srcChannel = new FileInputStream(srcPath).getChannel();
		    FileChannel destChannel = new FileInputStream(destPath).getChannel()){
			srcChannel.transferTo(0, srcChannel.size(), destChannel);
		}
	}

	public static void extractResource(String from, File to, boolean force, boolean lang){
		File of = to;

		// if to path is directory, cast to File. return if not file or directory
		if(to.isDirectory()){
			String filename = new File(from).getName();
			of = new File(to, filename);
		}else if(! of.isFile()){
			ActionLog.getInstance().warn("Not a file:" + of);
			return;
		}

		// if file exist, check force flag
		if(of.exists() && ! force){
			return;
		}

		OutputStream out = null;
		InputStream in = null;
		InputStreamReader reader = null;
		OutputStreamWriter writer = null;
		try{
			// get inside jar resource uri
			URL res = MCBansBungee.class.getResource(from);
			if(res == null){
				ActionLog.getInstance().warn("Can't find " + from + " in plugin jar file");
				return;
			}
			URLConnection resConn = res.openConnection();
			resConn.setUseCaches(false);
			in = resConn.getInputStream();

			if(in == null){
				ActionLog.getInstance().warn("Can't get input stream from " + res);
			}else{
				// write file
				if(lang){
					reader = new InputStreamReader(in, StandardCharsets.UTF_8);
					writer = new OutputStreamWriter(new FileOutputStream(of)); // not specify output encode

					int text;
					while((text = reader.read()) != - 1){
						writer.write(text);
					}
				}else{
					out = new FileOutputStream(of);
					byte[] buf = new byte[1024]; // Buffer size
					int len;
					while((len = in.read(buf)) >= 0){
						out.write(buf, 0, len);
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			// close stream
			try{
				if(out != null) out.close();
			}catch(Exception ignore){
			}
			try{
				if(in != null) in.close();
			}catch(Exception ignore){
			}
			try{
				if(reader != null) reader.close();
			}catch(Exception ignore){
			}
			try{
				if(writer != null) writer.close();
			}catch(Exception ignore){
			}
		}
	}

	public static File getPluginDir(){
		return MCBansBungee.getInstance().getDataFolder();
	}
}
