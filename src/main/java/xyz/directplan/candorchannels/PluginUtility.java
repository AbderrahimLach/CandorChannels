package xyz.directplan.candorchannels;

import org.bukkit.ChatColor;

/**
 * @author DirectPlan
 */
public class PluginUtility {

    public static String translateMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
