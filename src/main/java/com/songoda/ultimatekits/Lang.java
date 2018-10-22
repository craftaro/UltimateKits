package com.songoda.ultimatekits;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum Lang {

    PREFIX("prefix", "&8[&9UltimateKits&8]"),

    NO_CONSOLE_ACCESS("no-console", "&cOnly players may do that."),

    NO_PERM("no-permission", "&cYou do not have permission to do that!"),

    PREVIEW_NO_KIT_SUPPLIED("no-kit-supplied", "&7Please include a kit to preview."),

    KIT_DOESNT_EXIST("kit-doesnt-exist", "&cThat kit does not exist."),

    KIT_ALREADY_EXISTS("kit-already-exist", "&cThat kit already exists."),

    PLAYER_NOT_FOUND("player-not-found", "&cThis player is not online or doesn't exist."),

    GUI_KIT_NAME("gui-kit-showableName", "&c{KIT}"),

    PREVIEWING_KIT("Previewing-kit", "&9You are now previewing kit &7{KIT}&9."),

    SIGN_TITLE("sign-title", "&8[&9PreviewKit&8]"),

    PREVIEW_SIGN_CREATED("sign-created", "&aPreview sign created for kit &7{KIT}&9."),

    PREVIEW_ONLY_HOLOGRAM("Preview-only-hologramHandler", "Click to Preview!"),

    PREVIEW_HOLOGRAM("Preview-hologramHandler", "Right-Click to Preview!"),

    BUY_LINK_HOLOGRAM("Buy-link-hologramHandler", "Left-Click for Buy Link!"),

    BUY_ECO_HOLOGRAM("Buy-eco-hologramHandler", "Left-Click to buy for &a${PRICE}&f!"),

    OPEN_CRATE_HOLOGRAM("open-crate-hologramHandler", "Left-Click with a key to open!"),
    DAILY_HOLOGRAM("daily-hologramHandler", "Left-Click to claim!"),

    WRONG_KEY("wrong-key", "&cThis key does not go to this kit."),
    NOT_KEY("not-key", "&cYou are not holding a key."),
    NOT_YET("not-yet", "&cYou need to wait &4{TIME} &cbefore you can use this."),

    CANNOT_AFFORD("Cannot-afford", "&9You cannot afford to buy kit &7{KIT}&9."),

    NOT_TWICE("Not-twice", "&9You can only receive this kit once."),
    PURCHASE_SUCCESS("Purchase_success", "&9You have purchased kit &7{KIT}&9."),
    GIVE_SUCCESS("Give_success", "&9You have received kit &7{KIT}&9."),

    NO_COMMANDS("No-commands", "&9There are no commands attached to this kit. You cannot buy a kit if there are no commands assigned to it."),

    YES("yes", "yes"),

    NO("no", "no"),

    DELAY("Delay", "&9Please wait {TIME}"),

    COMMAND("Command", "&7Command"),

    MONEY("Money", "&6Money"),

    LINK("Link", "&9Link"),
    BUYNOW("Buy-now", "&aBuy Now"),
    BACK("Back", "&9Back"),
    EXIT("Exit", "&cExit"),

    BUYCANCELLED("buy-cancelled", "&cPurchase Cancelled."),

    TIMEOUT("buy-timeout", "&cPurchase timed out."),

    AREYOUSURE("are-you-sure", "&9Are you sure you would like to buy this kit for &a${PRICE}&9?"),

    YESORNO("yesorno", "&9Type &7Yes &9or &7no&9 into the chat."),

    SYNTAX("Synax", "&9Incorrect syntax. Please refer to /KP help"),

    CLICKLINK("Click-link", "&7Click to get Link."),

    CLICKECO("Click-eco", "&7Click to buy for &a${PRICE}&7."),

    PREVIEW("Preview", "&7Click to preview."),

    ALLKITS("All-kit", "&9All kit"),
    ALLKITS_LORE("All-kit-lore", "&7Display all kit."),

    ALLSALE("All-sale", "&9All For Sale kit"),
    ALLSALE_LORE("All-sale-lore", "&7Display all kit for sale."),

    UNLOCKED("Unlocked", "&9All Unlocked kit"),
    UNLOCKED_LORE("Unlocked-lore", "&7Display all unlocked kit."),

    FREE("Free", "Free"),

    GUI_TITLE_YESNO("gui-title-yesno", "&9Buy for &a${COST}&9?"),

    YES_GUI("yes-gui", "&a&lYes"),

    NO_GUI("no-gui", "&c&lNo"),

    PREVIEW_TITLE("preview-title", "&9Previewing kit: &8{KIT}"),

    KEY_TITLE("key-title", "&5{KIT} &fKit Key"),

    KEY_GIVEN("Key-given", "&9You have received a &a{KIT} &9kit key."),

    KEY_SUCCESS("key-success", "&9You have successfully redeemed a key for the kit &7{KIT}&9."),

    KEY_DESC1("key-desc1", "&rRight-Click on [a ]&c&l{KIT}&r kit"),

    KEY_DESC2("key-desc2", "&rand receive its contents!"),

    KEY_DESC3("key-desc3", "&rand receive some of its contents!"),

    KEY_DESC4("key-desc4", "&rGives kit &c&l{AMT} &rtimes."),

    KITS_TITLE("kit-title", "&8Server kits"),

    DETAILS("Details", "&7Hello &e{PLAYER}&7!|&7Listed below are our servers kit.||&7Click on the &eicon &7representing the &ekit |&7inorder to &epreview, claim or buy &7it."),

    ABOUT_KIT("About-kit", "&7Can't open a kit?|&7Rank up to gain access!"),

    ONCE("Once", "&7Cooldown: &6You already claimed this kit!"),
    READY("Ready", "&7Cooldown: &6Ready for use!"),
    PLEASE_WAIT("Please-wait", "&7Cooldown: &6{TIME}"),
    NO_ACCESS("No-access", "&7Cooldown: &cNo Access.."),

    LEFT_PREVIEW("Left_preview", "&6&lLEFT CLICK &7to preview kit."),
    RIGHT_CLAIM("Right_claim", "&6&lRIGHT CLICK &7to claim kit."),
    RIGHT_BUY("Right_buy", "&6&lRIGHT CLICK &7to buy kit."),

    NEXT("Next", "&7Next Page"),
    LAST("Last", "&7Last Page"),
    ECO_SENT("Eco_Sent", "&7You received &a{AMT}&7."),

    INVENTORY_FULL("Inventory-full", "&cYour inventory is too full to claim this kit!");


    private static FileConfiguration LANG;
    private String path;
    private String def;

    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }

    public static void setFile(final FileConfiguration config) {
        LANG = config;
    }

    public String getDefault() {
        return this.def;
    }

    public String getPath() {
        return this.path;
    }

    public String getConfigValue(Object ob) {
        String value = ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));

        if (ob != null) {
            value = value.replace("{PLAYER}", ob.toString());
            value = value.replace("{KIT}", ob.toString());
            value = value.replace("{AMT}", ob.toString());
            value = value.replace("{COST}", ob.toString());
            value = value.replace("{PRICE}", ob.toString());
            value = value.replace("{TIME}", ob.toString());
        }
        return value;
    }

    public String getConfigValue() {
        String value = ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));
        return value;
    }
}
