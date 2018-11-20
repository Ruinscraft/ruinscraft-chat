package com.ruinscraft.chat.channel;

public class ChatChannelManager {

	private ChatChannel globalChannel;
	private ChatChannel localChannel;
	
	public ChatChannelManager(String localChannelImplementation) {
		this.globalChannel = new GlobalChatChannel();
		
		switch (localChannelImplementation) {
		
		}
	}
	
	public ChatChannel getGlobalChannel() {
		return globalChannel;
	}
	
	public ChatChannel getLocalChannel() {
		return localChannel;
	}
	
}
