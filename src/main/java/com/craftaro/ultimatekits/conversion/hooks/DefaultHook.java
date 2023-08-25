package com.craftaro.ultimatekits.conversion.hooks;

import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.ultimatekits.conversion.Hook;
import com.craftaro.ultimatekits.utils.ItemSerializer;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class DefaultHook implements Hook {
    public Set<ItemStack> getItems(String kitName) {
        Set<ItemStack> items = new HashSet<>();

        for (Kits kit : Kits.values()) {
            if (!kit.name().equalsIgnoreCase(kitName)) {
                continue;
            }

            for (String string : kit.items) {
                items.add(ItemSerializer.deserializeItemStackFromJson(string));
            }
        }

        return items;
    }

    public Set<String> getKits() {
        Set<String> kits = new HashSet<>();

        for (Kits kit : Kits.values()) {
            if (kit == Kits.BRIANNA_1_12 && ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                    || kit == Kits.BRIANNA_1_13 && !ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                continue;
            }
            kits.add(kit.name().toLowerCase().replace("_1_12", "").replace("_1_13", ""));
        }

        return kits;
    }

    public long getDelay(String kitName) {
        for (Kits kit : Kits.values()) {
            if (!kit.name().equalsIgnoreCase(kitName)) {
                continue;
            }
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

        BRIANNA_1_12(0, "{id:\"minecraft:skull\",Count:1b,tag:{SkullOwner:{Id:\"2626974f-5838-44c6-994d-f6c723d40b79\",Properties:{textures:[0:{Signature:\"tQRjs3H+5H5A8Id/Hb2a3E+VDQY8sUKQYacc9ZYDsBncagNO6imX6h6qsDmtTDtliTh8ZzHRIAw/6gQeiODJnOm4PAB2vPnUv0VFAP2awbikJeOSuK3WZsvcUN78+hXLFFtvBUt/oGKpsTj5P//2+qH7QLqmrxFD72jGpmyS8Fp6AHuQqzeHn40isvKT2+oTItFb3wQ3H2QgudGxEhOX6Onz0M2xmH61+HF5ajPS+/2hUl5kjRk8uRLc2AJD162dECdYOUJ8/j47Fl9pJXG1dcG6cGMuRlDAim3AqfdJFTW0CI78YEpTTGZcLL+O+/yUMQD9hOQqjP7y63YIOZ9kAZ7DRzu5hQaHLevhUcTwZnkqs+F9eUuQR00y8PoVoSEOMrV4LIWAJFutdziNZX9I7MI9tJ0x96imA18yYC9Skwx7JHurC1wpdtSqS/F7gCeU3+2QiFufxX9ft9BPHSBX7z08nqQkwxYh0O/06OX+H0NJTLwYrK+hoTXT1A5dNrJIHzGuK9/qUaitH4yz0zv5aGd3vVytdKCULFqOTiqur1QYPvF9sI2fhvfcHqUc30yU1di0Ws5SOQzCAFLOypjUH1ezQMOK+Xy3ixB0a8HXI9RL8ewOvKaVepbWS1vomNNWfiCgpudbLW8x6bPvQLmZn8YWPjCAL0WFycnw8lm6JDg=\",Value:\"eyJ0aW1lc3RhbXAiOjE1NzA5MDI0MDg4ODEsInByb2ZpbGVJZCI6IjA2Y2NiMGU0MWM3NjQ0YjE5OGZiYzk5OTBhODcwNjAwIiwicHJvZmlsZU5hbWUiOiJCcmlhbm5hIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yN2I3MjM5Yzk2OWNhNzY0NWJmMTQzODc4M2Y1Y2FiZjdlMDhhYTViODY4OGFkODg3NWJhNzNlNTVhNDBlMjIiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ==\"}]},Name:\"Brianna\"}},Damage:3s}}"),

        BRIANNA_1_13(0, "{id:\"minecraft:player_head\",Count:1b,tag:{SkullOwner:{Id:\"2626974f-5838-44c6-994d-f6c723d40b79\",Properties:{textures:[{Signature:\"tQRjs3H+5H5A8Id/Hb2a3E+VDQY8sUKQYacc9ZYDsBncagNO6imX6h6qsDmtTDtliTh8ZzHRIAw/6gQeiODJnOm4PAB2vPnUv0VFAP2awbikJeOSuK3WZsvcUN78+hXLFFtvBUt/oGKpsTj5P//2+qH7QLqmrxFD72jGpmyS8Fp6AHuQqzeHn40isvKT2+oTItFb3wQ3H2QgudGxEhOX6Onz0M2xmH61+HF5ajPS+/2hUl5kjRk8uRLc2AJD162dECdYOUJ8/j47Fl9pJXG1dcG6cGMuRlDAim3AqfdJFTW0CI78YEpTTGZcLL+O+/yUMQD9hOQqjP7y63YIOZ9kAZ7DRzu5hQaHLevhUcTwZnkqs+F9eUuQR00y8PoVoSEOMrV4LIWAJFutdziNZX9I7MI9tJ0x96imA18yYC9Skwx7JHurC1wpdtSqS/F7gCeU3+2QiFufxX9ft9BPHSBX7z08nqQkwxYh0O/06OX+H0NJTLwYrK+hoTXT1A5dNrJIHzGuK9/qUaitH4yz0zv5aGd3vVytdKCULFqOTiqur1QYPvF9sI2fhvfcHqUc30yU1di0Ws5SOQzCAFLOypjUH1ezQMOK+Xy3ixB0a8HXI9RL8ewOvKaVepbWS1vomNNWfiCgpudbLW8x6bPvQLmZn8YWPjCAL0WFycnw8lm6JDg=\",Value:\"eyJ0aW1lc3RhbXAiOjE1NzA5MDI0MDg4ODEsInByb2ZpbGVJZCI6IjA2Y2NiMGU0MWM3NjQ0YjE5OGZiYzk5OTBhODcwNjAwIiwicHJvZmlsZU5hbWUiOiJCcmlhbm5hIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yN2I3MjM5Yzk2OWNhNzY0NWJmMTQzODc4M2Y1Y2FiZjdlMDhhYTViODY4OGFkODg3NWJhNzNlNTVhNDBlMjIiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ==\"}]},Name:\"Brianna\"},Damage:3}}");

        public final String[] items;
        public final int delay;

        Kits(int delay, String... items) {
            this.items = items;
            this.delay = delay;
        }
    }
}
