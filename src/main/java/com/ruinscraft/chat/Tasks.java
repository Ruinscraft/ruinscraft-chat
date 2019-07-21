package com.ruinscraft.chat;

public final class Tasks {

    public static void async(Runnable task) {
        ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), task);
    }

}
