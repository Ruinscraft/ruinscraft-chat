package com.ruinscraft.chat.api;

import java.util.Optional;
import java.util.function.Function;

public interface IMessageFilter {

    String getName();

    Function<String, Optional<String>> getFunction();

}
