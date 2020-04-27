package com.ruinscraft.chat.api;

import java.util.Set;
import java.util.function.Function;

public interface IMessageFormatter {

    Set<Function<String, String>> getReplacements();

}
