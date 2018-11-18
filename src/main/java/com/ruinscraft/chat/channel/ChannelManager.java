package com.ruinscraft.chat.channel;

public class ChannelManager {

	private ChatChannel globalChannel;
	private ChatChannel localChannel;
	
	public ChannelManager(String localChannelImplementation) {
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
