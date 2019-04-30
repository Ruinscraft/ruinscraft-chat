package com.ruinscraft.chat.filters;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

public class ChatFilterManager {

    private Set<ChatFilter> filters;

    public ChatFilterManager(ConfigurationSection filtersSection) {
        /* Setup filters */
        filters = new HashSet<>();
        filters.add(new CapsFilter());
        filters.add(new LengthFilter());
        filters.add(new ASCIIFilter());
        filters.add(new ProfanityFilter(filtersSection.getConfigurationSection("profanity")));
    }

    public Set<ChatFilter> getChatFilters() {
        return filters;
    }

    public <T extends ChatFilter> ChatFilter getByType(Class<T> clazz) {
        for (ChatFilter chatFilter : filters) {
            if (chatFilter.getClass().getName().equals(clazz.getName())) {
                return chatFilter;
            }
        }
        return null;
    }

}
