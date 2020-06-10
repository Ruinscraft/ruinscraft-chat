package com.ruinscraft.chat.api;

public interface IChatCommandExec {

    /*
     *  Permissions don't need to be checked here. They should be handled
     *  by the platform such as Bukkit with plugin.yml
     */
    void onExecute(IChatPlayer executor, String label, String[] args);

}
