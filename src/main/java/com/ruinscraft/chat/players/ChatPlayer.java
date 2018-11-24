package com.ruinscraft.chat.players;

import java.util.UUID;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;

/*
 * TODO:
 * 		Implement ignoring
 * 		Implement muted channels
 * 		Other stuff?
 */
public class ChatPlayer {

	private UUID mojangUUID;
	private ChatChannel<GenericChatMessage> focused;

	public ChatPlayer(UUID mojangUUID) {
		this.mojangUUID = mojangUUID;
	}

	public ChatPlayer(UUID mojangUUID, ChatChannel<GenericChatMessage> focused) {
		this.mojangUUID = mojangUUID;
		this.focused = focused;
	}

	public void setMojangUUID(UUID mojangUUID) {
		this.mojangUUID = mojangUUID;
	}
	
	public UUID getMojangUUID() {
		return mojangUUID;
	}
	
	public void setFocused(ChatChannel<GenericChatMessage> focused) {
		this.focused = focused;
	}
	
	public ChatChannel<GenericChatMessage> getFocused() {
		return focused == null ? focused = ChatPlugin.getInstance().getChatChannelManager().getByName("global") : focused;
	}

}
