package com.ruinscraft.chat.core.filter;

import com.ruinscraft.chat.api.IMessageFilter;

import java.util.Optional;
import java.util.function.Function;

public class MessageFilter implements IMessageFilter {

    private String name;
    private Function<String, Optional<String>> function;

    public MessageFilter(String name, Function<String, Optional<String>> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Function<String, Optional<String>> getFunction() {
        return function;
    }

}
