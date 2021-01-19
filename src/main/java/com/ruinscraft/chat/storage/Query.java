package com.ruinscraft.chat.storage;

import java.util.ArrayList;
import java.util.List;

public class Query<T> {

    private List<T> results;

    public Query() {
        results = new ArrayList<>();
    }

    public int getCount() {
        return results.size();
    }

    public boolean hasResults() {
        return getCount() > 0;
    }

    public List<T> getResults() {
        return results;
    }

    public void addResult(T result) {
        results.add(result);
    }

    public T getFirst() {
        if (hasResults()) {
            return results.get(0);
        } else {
            return null;
        }
    }

}
