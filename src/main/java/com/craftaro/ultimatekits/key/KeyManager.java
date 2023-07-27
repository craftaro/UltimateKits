package com.craftaro.ultimatekits.key;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class KeyManager {

    private final Set<Key> registeredKeys = new HashSet<>();

    public boolean addKey(Key key) {
        if (key == null) return false;
        return registeredKeys.add(key);
    }

    public void removeKey(Key key) {
        registeredKeys.remove(key);
    }

    public Key getKey(String name) {
        for (Key key : registeredKeys)
            if (key.getName().equalsIgnoreCase(name)) return key;
        return null;
    }


    public Set<Key> getKeys() {
        return Collections.unmodifiableSet(registeredKeys);
    }

    public void clear() {
        registeredKeys.clear();
    }
}
