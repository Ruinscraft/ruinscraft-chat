package com.ruinscraft.chat.filters;

public class NotSendableException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotSendableException(String reason) {
        super(reason);
    }

}
