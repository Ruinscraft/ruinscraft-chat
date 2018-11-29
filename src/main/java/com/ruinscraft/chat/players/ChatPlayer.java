package com.ruinscraft.chat.players;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.GenericChatMessage;

public class ChatPlayer {

	private UUID mojangUUID;

	private int chatPlayerId;
	private ChatChannel<GenericChatMessage> focused;
	private String nickname;

	public Set<MinecraftIdentity> ignoring;
	public Set<ChatChannel<? extends ChatMessage>> muted;

	protected ChatPlayer(UUID mojangUUID) {
		this.mojangUUID = mojangUUID;
		this.ignoring = new HashSet<>();
		this.muted = new HashSet<>();
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

	public boolean ignore(MinecraftIdentity minecraftIdentity) {
		boolean success = ignoring.add(minecraftIdentity);

		if (success) {
			save();
		}
		
		return success;
	}

	public boolean unignore(MinecraftIdentity minecraftIdentity) {
		boolean success = ignoring.remove(minecraftIdentity);
		
		if (success) {
			save();
		}
		
		return success;
	}

	public boolean mute(ChatChannel<? extends ChatMessage> chatChannel) {
		boolean success = muted.add(chatChannel);

		if (success) {
			save();
		}
		
		return success;
	}

	public boolean unmute(ChatChannel<? extends ChatMessage> chatChannel) {
		boolean success = muted.remove(chatChannel);
		
		if (success) {
			save();
		}
		
		return success;
	}

	public void save() {
		ChatPlugin.getInstance().getChatPlayerManager().save(this);
	}

}
