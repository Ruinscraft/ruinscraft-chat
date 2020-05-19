package com.ruinscraft.chat.core;

import java.util.logging.Logger;

public interface ChatPlatform {

    ChatConfig loadConfigFromDisk();

    Logger getJLogger();

}
