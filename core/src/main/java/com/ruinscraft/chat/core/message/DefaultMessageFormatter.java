package com.ruinscraft.chat.core.message;

import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IMessageFormatter;

public class DefaultMessageFormatter implements IMessageFormatter {

    /*
     *  DISPLAYNAME > CONTENT
     *
     *  DISPLAYNAME should be setup on the platform to include things which are relevant
     *  like a group prefix.
     *
     *  Ex:
     *  [Sponsor] foobar > hello
     *
     *
     *  "[Sponsor] foobar"  is the DISPLAYNAME
     *  "hello"             is the CONTENT
     */

    @Override
    public String getFormat() {
        return "%displayname% > %content%";
    }

    @Override
    public String format(IChatMessage input, Object... replacements) {
        String format = getFormat();

        format.replace("%displayname%", input.getSender().getDisplayName());
        format.replace("%content%", input.getContent());

        return format;
    }

}
