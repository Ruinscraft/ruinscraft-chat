package com.ruinscraft.chat.players;

import java.util.Set;
import java.util.UUID;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.GenericChatMessage;

public class ChatPlayer {

	protected UUID mojangUUID;
	
	protected int chatPlayerId;
	protected ChatChannel<GenericChatMessage> focused;
	protected String nickname;
	
	protected Set<MinecraftIdentity> ignoring;
	protected Set<ChatChannel<? extends ChatMessage>> muted;

	protected ChatPlayer(UUID mojangUUID) {
		this.mojangUUID = mojangUUID;
	}
	
	public void setMojangUUID(UUID mojangUUID) {
		this.mojangUUID = mojangUUID;
	}
	
	public UUID getMojangUUID() {
		return mojangUUID;
	}
	
	public void setChatPlayerId(int chatPlayerId) {
		this.chatPlayerId = chatPlayerId;
	}
	
	public int getChatPlayerId() {
		return chatPlayerId;
	}
	
	public void setFocused(ChatChannel<GenericChatMessage> chatChannel) {
		if (this.focused != null & (this.focused = chatChannel) != null) {
			save();
		}
	}
	
	public void setFocused(String channelName) {
		setFocused(ChatPlugin.getInstance().getChatChannelManager().getByName(channelName));
	}
	
	public ChatChannel<GenericChatMessage> getFocused() {
		return focused == null ? focused = ChatPlugin.getInstance().getChatChannelManager().getDefaultChatChannel() : focused;
	}
	
	public void setNickname(String nickname) {
		if (this.nickname != null & (this.nickname = nickname) != null) {
			save();
		}
	}
	
	public String getNickname() {
		return nickname;
	}

	public boolean ignore(MinecraftIdentity minecraftIdentity, boolean save) {
		boolean success = ignoring.add(minecraftIdentity);
		if (success && save) {
			save();
		}
		return success;
	}
	
	public boolean unignore(MinecraftIdentity minecraftIdentity, boolean save) {
		boolean success = ignoring.remove(minecraftIdentity);
		if (success && save) {
			save();
		}
		return success;
	}
	
	public Set<MinecraftIdentity> getIgnoring() {
		return ignoring;
	}
	
	public boolean mute(ChatChannel<? extends ChatMessage> chatChannel, boolean save) {
		boolean success = muted.add(chatChannel);
		if (success && save) {
			save();
		}
		return success;
	}
	
	public boolean unmute(ChatChannel<? extends ChatMessage> chatChannel, boolean save) {
		boolean success = muted.remove(chatChannel);
		if (success && save) {
			save();
		}
		return success;
	}
	 
	public Set<ChatChannel<? extends ChatMessage>> getMuted() {
		return muted;
	}
	
	public void save() {
		ChatPlugin.getInstance().getChatPlayerManager().save(this);
	}
	
}
