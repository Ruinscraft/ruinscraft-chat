package com.ruinscraft.chat.players;

import java.util.UUID;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;

/*
 * TODO:
 * 		- Implement ignoring
 * 		- Implement muted channels
 */
public class ChatPlayer {

	private final UUID mojangUUID;
	
	private int chatPlayerId;
	private ChatChannel<GenericChatMessage> focused;
	private String nickname;

	public ChatPlayer(UUID mojangUUID) {
		this.mojangUUID = mojangUUID;
	}

	public UUID getMojangUUID() {
		return mojangUUID;
	}
	
	public void setChatPlayerId(int chatPlayerId) {
		this.chatPlayerId = chatPlayerId;
		save();
	}
	
	public int getChatPlayerId() {
		return chatPlayerId;
	}
	
	public void setFocused(ChatChannel<GenericChatMessage> chatChannel) {
		this.focused = chatChannel;
		save();
	}
	
	public void setFocused(String channelName) {
		setFocused(ChatPlugin.getInstance().getChatChannelManager().getByName(channelName));
	}
	
	public ChatChannel<GenericChatMessage> getFocused() {
		return focused == null ? focused = ChatPlugin.getInstance().getChatChannelManager().getByName("global") : focused;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
		save();
	}
	
	public String getNickname() {
		return nickname;
	}

	public void save() {
		ChatPlugin.getInstance().getChatPlayerManager().save(this);
	}
	
}
