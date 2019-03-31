package com.ruinscraft.chat.commands;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.ChatUtil;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.players.ChatPlayer;

/**
 *	A command to bring up a GUI menu for chat options (muting channels, enable/disable PMS, etc).
 */
public class ChatCommand implements CommandExecutor, Listener {

	private static final String INVENTORY_NAME = "Chat Menu";

	private static final Material CHAT_ON;
	private static final Material CHAT_OFF;

	static {
		if (Bukkit.getVersion().contains("1.13")) {
			CHAT_ON = Material.MUSIC_DISC_CHIRP;
			CHAT_OFF = Material.MUSIC_DISC_CAT;
		} else {
			CHAT_ON = Material.GRASS;
			CHAT_OFF = Material.GRASS;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return false;

		Player player = (Player) sender;
		ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());
		Set<ChatChannel<?>> muteableChannels = ChatPlugin.getInstance().getChatChannelManager().getMuteableChannels();

		int slots = ChatUtil.getInventorySlotCount(muteableChannels.size());
		Inventory chatMenu = Bukkit.createInventory(null, slots, INVENTORY_NAME);

		for (ChatChannel<?> channel : muteableChannels) {
			ItemStack channelItem = new ItemStack(CHAT_ON);
			ItemMeta channelItemMeta = channelItem.getItemMeta();
			channelItemMeta.setDisplayName("Mute " + channel.getPrettyName());

			if (chatPlayer.isMuted(channel)) {
				channelItem = new ItemStack(CHAT_OFF);
				channelItemMeta.setDisplayName("Unmute " + channel.getPrettyName());
			}

			channelItem.setItemMeta(channelItemMeta);
			chatMenu.addItem(channelItem);
		}

		player.openInventory(chatMenu);

		return true;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;

		Player player = (Player) event.getWhoClicked();
		ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());

		if (player.getOpenInventory() == null) return;

		if (player.getOpenInventory().getTitle().equals(INVENTORY_NAME)) {
			event.setResult(Result.DENY);
			event.setCancelled(true);

			ItemStack clicked = event.getCurrentItem();

			if (clicked == null || clicked.getType() == Material.AIR) return;

			int muteableChannelsSize = ChatPlugin.getInstance().getChatChannelManager().getMuteableChannels().size();
			if (event.getSlot() > muteableChannelsSize) return;

			if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
				String name = clicked.getItemMeta().getDisplayName();
				boolean unmute = name.toLowerCase().contains("unmute");
				if (unmute) {
					String channelName = name.replace("Unmute", "").trim();
					chatPlayer.unmute(channelName);
					player.sendMessage(Constants.COLOR_BASE + "You have unmuted " + Constants.COLOR_ACCENT + channelName);

					clicked.setType(CHAT_ON);
					ItemMeta itemMeta = clicked.getItemMeta();
					itemMeta.setDisplayName("Mute " + channelName);
					clicked.setItemMeta(itemMeta);
				} else {
					String channelName = name.replace("Mute", "").trim();
					chatPlayer.mute(channelName);
					player.sendMessage(Constants.COLOR_BASE + "You have muted " + Constants.COLOR_ACCENT + channelName);

					clicked.setType(CHAT_OFF);
					ItemMeta itemMeta = clicked.getItemMeta();
					itemMeta.setDisplayName("Unmute " + channelName);
					clicked.setItemMeta(itemMeta);
				}
			}
		}
	}

}
