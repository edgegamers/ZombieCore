package xyz.msws.zombie.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Helper class for sending messages
 */
public class MSG {
    public static ChatColor ALL = ChatColor.WHITE;
    public static ChatColor PLAYER = ChatColor.YELLOW;
    public static ChatColor STAFF = ChatColor.GOLD;

    public static ChatColor ADMIN = ChatColor.RED;

    public static final ChatColor DEFAULT = ChatColor.GRAY;

    public static ChatColor FORMATTER = ChatColor.GRAY;
    public static final ChatColor FORMAT_INFO = ChatColor.GREEN;
    public static ChatColor FORMAT_SEPARATOR = ChatColor.YELLOW;

    public static final ChatColor NUMBER = ChatColor.YELLOW;
    public static ChatColor TIME = ChatColor.GOLD;
    public static ChatColor DATE = ChatColor.DARK_GREEN;
    public static ChatColor MONEY = ChatColor.GREEN;

    public static final ChatColor SUBJECT = ChatColor.AQUA;

    public static final ChatColor PREFIX = ChatColor.BLUE;

    public static ChatColor ERROR = ChatColor.RED;
    public static ChatColor FAIL = ChatColor.RED;
    public static final ChatColor SUCCESS = ChatColor.GREEN;

    public static final ChatColor BOLD = ChatColor.BOLD;
    public static ChatColor ITALIC = ChatColor.ITALIC;
    public static ChatColor MAGIC = ChatColor.MAGIC;
    public static ChatColor UNDER = ChatColor.UNDERLINE;
    public static ChatColor STRIKE = ChatColor.STRIKETHROUGH;
    public static final ChatColor RESET = ChatColor.RESET;

    /**
     * Sends the specified string colored
     * Formats the string if additional parameters are given
     *
     * @param sender  {@link CommandSender} recipient
     * @param message Message to send
     * @param format  Objects to format message with
     */
    public static void tell(CommandSender sender, String message, Object... format) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, format)));
    }

    /**
     * Sends a {@link Sendable} message to the target
     * Formats the message if additional parameters are given
     *
     * @param sender  {@link CommandSender} recipient
     * @param message Sendable to send
     * @param format  Objects to format message with
     */
    public static void tell(CommandSender sender, Sendable message, Object... format) {
        tell(sender, message.format(format));
    }

    /**
     * Logs the specified message to console
     *
     * @param message Message to send
     * @param format  Objects to format message with
     */
    public static void log(String message, Object... format) {
        tell(Bukkit.getConsoleSender(), message, format);
    }

    /**
     * Sends the specified string with <b>no coloring</b>
     * Formats the string if additional parameters are given
     *
     * @param sender  {@link CommandSender} recipient
     * @param message Message to send
     * @param format  Objects to format message with
     */
    public static void tellRaw(CommandSender sender, String message, Object... format) {
        sender.sendMessage(String.format(message, format));
    }

    /**
     * Returns the string with {@link ChatColor#translateAlternateColorCodes(char, String)} applied
     *
     * @param s String to color
     * @return Colored string
     */
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /**
     * Returns the string with {@link ChatColor#stripColor(String)} applied
     *
     * @param s String to strip
     * @return Stripped string
     */
    public static String stripColor(String s) {
        return ChatColor.stripColor(s);
    }

    /**
     * Simplifies duration descriptions
     *
     * @param ms Milliseconds for the duration
     * @return Formatted description of duration
     */
    public static String getDuration(long ms) {
        TimeUnit unit = TimeUnit.DAYS;
        for (TimeUnit u : TimeUnit.values()) {
            if (ms < u.toMillis(1))
                break;
            unit = u;
        }

        double p = (double) ms / unit.toMillis(1);

        if (p == 1) // If it's exactly 1 unit, don't have an S at the end of the unit
            return "1 " + unit.toString().toLowerCase().substring(0, unit.toString().length() - 1);

        if (p == (int) p) // If it's exactly a number of units, don't specify decimals
            return (int) p + " " + unit.toString().toLowerCase();

        return String.format("%.2f %s", (double) ms / unit.toMillis(1), unit.toString().toLowerCase());
    }

    /**
     * Simplify a string for easy key usage
     *
     * @param value
     * @return a-zA-Z regex compatible
     */
    public static String normalize(String value) {
        return value.toLowerCase().replaceAll("[^a-z]", "");
    }

    /**
     * Returns string with camel case, and with _'s replaced with spaces
     *
     * @param string hello_how is everyone
     * @return Hello How Is Everyone
     */
    public static String camelCase(String string) {
        String prevChar = " ";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (i > 0)
                prevChar = string.charAt(i - 1) + "";
            if (prevChar.matches("[a-zA-Z]")) {
                res.append((string.charAt(i) + "").toLowerCase());
            } else {
                res.append((string.charAt(i) + "").toUpperCase());
            }
        }
        return res.toString().replace("_", " ");
    }

    public static void warn(String message) {
        Bukkit.getLogger().log(Level.WARNING, message);
    }

    public static String theme() {
        return "&e";
    }

}
