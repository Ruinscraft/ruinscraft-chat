package com.ruinscraft.chat.channel.types.pm;

public interface ReplyCache {

	String getReply(String username);
	
	void setReply(String username, String _username);
	
}
