package com.ruinscraft.chat.storage;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.MailMessage;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.FriendRequest;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import com.ruinscraft.chat.player.PersonalizationSettings;
import com.ruinscraft.chat.storage.query.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class ChatStorage {

    public abstract CompletableFuture<Void> saveChatPlayer(ChatPlayer chatPlayer);

    public abstract CompletableFuture<ChatPlayerQuery> queryChatPlayer(UUID mojangId);

    public abstract CompletableFuture<ChatPlayerQuery> queryChatPlayer(String username);

    public abstract CompletableFuture<Void> saveChatMessage(ChatMessage chatMessage);

    public abstract CompletableFuture<ChatMessageQuery> queryChatMessage(UUID chatMessageId);

    public abstract CompletableFuture<Void> saveOnlineChatPlayer(OnlineChatPlayer chatPlayer);

    public abstract CompletableFuture<OnlineChatPlayerQuery> queryOnlineChatPlayers();

    public abstract CompletableFuture<Void> saveFriendRequest(FriendRequest friendRequest);

    public abstract CompletableFuture<Void> deleteFriendRequest(FriendRequest friendRequest);

    public abstract CompletableFuture<FriendRequestQuery> queryFriendRequests(OnlineChatPlayer onlineChatPlayer);

    public abstract CompletableFuture<Void> deleteOfflineChatPlayers();

    public abstract CompletableFuture<Void> saveMailMessage(MailMessage mailMessage);

    public abstract CompletableFuture<MailMessageQuery> queryMailMessages(ChatPlayer chatPlayer);

    public abstract CompletableFuture<ChatPlayerQuery> queryBlocked(ChatPlayer chatPlayer);

    public abstract CompletableFuture<Void> insertBlock(ChatPlayer blocker, ChatPlayer blocked);

    public abstract CompletableFuture<Void> deleteBlock(ChatPlayer blocker, ChatPlayer blocked);

    public abstract CompletableFuture<Void> insertActiveChannel(ChatPlayer chatPlayer, ChatChannel channel);

    public abstract CompletableFuture<Void> deleteActiveChannel(ChatPlayer chatPlayer, ChatChannel channel);

    public abstract CompletableFuture<FocusedChatChannelNameQuery> queryFocusedChannels(ChatPlayer chatPlayer);

    public abstract CompletableFuture<Void> savePersonalizationSettings(ChatPlayer chatPlayer, PersonalizationSettings personalizationSettings);

    public abstract CompletableFuture<PersonalizationSettingsQuery> queryPersonalizationSettings(ChatPlayer chatPlayer);

}
