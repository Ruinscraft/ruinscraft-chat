package com.ruinscraft.chat.channel.types.pm;

import java.util.concurrent.Callable;

public interface ReplyStorage extends AutoCloseable {

	Callable<String> getReply(String username);
	
	void setReply(String username, String _username);

	@Override
	default void close() {}
	
}
