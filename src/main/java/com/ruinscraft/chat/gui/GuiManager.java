package com.ruinscraft.chat.gui;

import com.ruinscraft.chat.ChatPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GuiManager {

    public final NamespacedKey chatAction;
    private ChatPlugin chatPlugin;
    private Map<String, Gui> guis;

    public GuiManager(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
        chatAction = new NamespacedKey(chatPlugin, "chat_action");
        guis = new HashMap<>();
    }

    public void registerGuis() {
        guis.put("chatsettings", new ChatSettingsGui("Chat Settings", 9, chatPlugin));

        for (Gui gui : guis.values()) {
            chatPlugin.getServer().getPluginManager().registerEvents(gui, chatPlugin);
        }
    }

    public void openGui(Player player, String name) {
        Gui gui = guis.get(name);

        if (gui != null) {
            gui.open(player);
        }
    }

}