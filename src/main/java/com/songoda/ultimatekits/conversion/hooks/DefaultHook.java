package com.songoda.ultimatekits.conversion.hooks;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.conversion.Hook;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class DefaultHook implements Hook {

    public Set<ItemStack> getItems(String kitName) {
        Set<ItemStack> items = new HashSet<>();

        for (Kits kit : Kits.values()) {
            if (!kit.name().equalsIgnoreCase(kitName)) continue;
            for (String string : kit.items) {
                items.add(UltimateKits.getInstance().getItemSerializer().deserializeItemStackFromJson(string));
            }
        }

        return items;
    }

    public Set<String> getKits() {
        Set<String> kits = new HashSet<>();

        for (Kits kit : Kits.values()) {
            kits.add(kit.name().toLowerCase());
        }

        return kits;
    }

    public long getDelay(String kitName) {
        for (Kits kit : Kits.values()) {
            if (!kit.name().equalsIgnoreCase(kitName)) continue;
            return kit.delay;
        }
        return 0;
    }

    public enum Kits {
        TOOLS(10, "{id:\"minecraft:stone_pickaxe\",Count:1b}",
                "{id:\"minecraft:stone_axe\",Count:1b}",
                "{id:\"minecraft:stone_hoe\",Count:1b}",
                "{id:\"minecraft:stone_shovel\",Count:1b}"),

        BETTER_TOOLS(300, "{id:\"minecraft:diamond_axe\",Count:1b,tag:{Enchantments:[{lvl:2s,id:\"minecraft:efficiency\"},{lvl:2s,id:\"minecraft:unbreaking\"}]}}",
                "{id:\"minecraft:diamond_pickaxe\",Count:1b,tag:{Enchantments:[{lvl:5s,id:\"minecraft:efficiency\"},{lvl:2s,id:\"minecraft:unbreaking\"}]}}",
                "{id:\"minecraft:diamond_shovel\",Count:1b,tag:{Enchantments:[{lvl:1s,id:\"minecraft:efficiency\"}]}}",
                "{id:\"minecraft:diamond_hoe\",Count:1b,tag:{Enchantments:[{lvl:3s,id:\"minecraft:unbreaking\"}]}}"),

        BRIANNA(0, "{id:\"minecraft:player_head\",Count:1b,tag:{SkullOwner:{Id:\"2626974f-5838-44c6-994d-f6c723d40b79\",Properties:{textures:[{Signature:\"Zx+6l8ZZACSIdWtHdllX6yqcLWC57ly7CTxO40SVbxwO3D1bMIQOjmeUbq3WkOb9lNIRvtH3oGcC6U9l1vICCIV3/oQZ4hxEs4yxmmGHmsK9Qm/oa4ZfaWNKdjio7kOWBqf8I+0zImXB1ptjAEfRJ1RCIaFW80amWLDeOhDhMfPTEOCP80ZfP29aGd91KPAVYOokvv/SX9BD4OaaStDt5Cbterfz0JwwBoogrYAIrkQuvxYlFkVHL1rO5ygLXiWGvF0lBqpeEX7r3QPT0D9iU92/zQ98v0N1klrOW4eK2tpAuhfI2mJ3nwKUKedssgAhmmEgG/doeByXJMlbfQxEfUifq7lHXm/sVD/Gd9pHJSMGQzenwhitiJpm7XHMRFUAJVi8Gt29TCXektC2E0VlsmDRQDb/fYQRPW2KgXpB2TpHzqFGMTjQCNtp4AEBgxaFrH87nXckkJGY+SFGUvcUE3D0u+026gwDUlrGB1Fr5leOF9tPSSu5r7h5i9t1m/LvO0SwPEl0UzeISX8JaJ0fLkvVmTV/lWGzXAZX7xgte0336uy0zYIovzE3wnDbb5iE9NM6+Zja4zU0PQi19gzK272zoC4SCEh6/h2yhU8cEtl8StoNSCk14puZz/XYCfbLpJBJz8x/E1R8WBbes0G2A5nsQGqMqWl0lz20zNd1eYk=\",Value:\"eyJ0aW1lc3RhbXAiOjE1NTY4MzcwMDk4NjksInByb2ZpbGVJZCI6IjI2MjY5NzRmNTgzODQ0YzY5OTRkZjZjNzIzZDQwYjc5IiwicHJvZmlsZU5hbWUiOiJTb25nb2RhIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zNTI4ZGZiNGYwZWUxNGQ4ZDhhM2RhMTM4YTYzYjZlOGI1MWJmZTIwNTYxZjhjZTliYjFhOTBhNjhjMTI5NWE1IiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=\"}]},Name:\"Songoda\"},Damage:3}}");

        public String[] items;
        public int delay;

        Kits(int delay, String... items) {
            this.items = items;
            this.delay = delay;
        }
    }
}
