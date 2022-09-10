package xyz.directplan.candorchannels.lib.config;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.directplan.candorchannels.PluginUtility;
import xyz.directplan.candorchannels.lib.config.replacement.Replacement;
import xyz.directplan.candorchannels.lib.config.replacement.ReplacementBoundary;
import xyz.directplan.candorchannels.user.User;

import java.util.*;

/**
 * @author DirectPlan
 */
public interface ConfigEntry {
    
    Object getValue();


    String getKey();

    Map<String, ReplacementBoundary> getReplacementBoundaries();

    void setValue(Object value);

    default boolean isForcedEntryDeclaration() { return true; }

    default List<String> getStringList(boolean colored, Replacement... replacements){
        Object value = getValue();
        List<String> tempStringList = new ArrayList<>();
        if(!(value instanceof List)) {
            return tempStringList;
        }
        List<?> list = (List<?>) value;
        for(Object obj : list) {
            if(obj instanceof String) {
                String v = proceedReplacements(obj.toString(), replacements);
                if(colored) {
                    v = PluginUtility.translateMessage(v);
                }
                if(v.contains("\n")) {
                    String[] lines = v.split("\n");
                    tempStringList.addAll(Arrays.asList(lines));
                    continue;
                }
                tempStringList.add(v);
            }
        }
        return tempStringList;
    }

    default List<String> getStringList(Replacement... replacements) {
        return getStringList(true, replacements);
    }

    default void broadcastToAll(TextComponent textComponent) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(textComponent);
        }
    }
    default void broadcastHoverMessage(String clickCommand, Replacement... replacements) {
        if(getValue() instanceof List) {
            List<String> lines = getStringList(replacements);
            for(String line : lines) {
                String translatedLine = PluginUtility.translateMessage(line);
                if(clickCommand != null) {
                    TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(translatedLine));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
                    broadcastToAll(textComponent);
                    continue;
                }
                Bukkit.broadcastMessage(translatedLine);
            }
            return;
        }
        String translatedMessage = PluginUtility.translateMessage(getStringValue(replacements));
        if(clickCommand != null) {
            TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(translatedMessage));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
            broadcastToAll(textComponent);
            return;
        }
        Bukkit.broadcastMessage(translatedMessage);
    }

    default void broadcastMessage(Replacement... replacements) {
        broadcastHoverMessage(null, replacements);
    }

    default void sendHoverMessage(User user, boolean hoverClick, String command, Replacement... replacements) {
        Player player = user.getPlayer();
        if(player != null && player.isOnline()) {
            sendHoverMessage(player, hoverClick, command, replacements);
        }
    }

    default void sendMessage(User user, Replacement... replacements) {
        sendHoverMessage(user, false, null, replacements);
    }

    default void sendMessage(CommandSender sender, Replacement... replacements) {
        sendHoverMessage(sender, false, null, replacements);
    }

    default void sendHoverMessage(CommandSender sender, boolean hoverClick, String command, Replacement... replacements) {
        Object value = getValue();
        if(value instanceof List) {
            List<String> lines = getStringList(replacements);
            for(String line : lines) {
                if(hoverClick) {
                    sendClickMessage(sender, command, line);
                    continue;
                }
                sender.sendMessage(line);
            }
            return;
        }
        String message = getStringValue(replacements);
        String translatedMessage = PluginUtility.translateMessage(message);

        if(hoverClick) {
            sendClickMessage(sender, command, message);
            return;
        }
        sender.sendMessage(translatedMessage);
    }

    default void sendClickMessage(CommandSender sender, String command, String message) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(message));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        player.spigot().sendMessage(textComponent);
    }

//    default void sendClickableMessage(Player player, String command, Replacement... replacements) {
//        String message = getStringValue(replacements);
//
//        TextComponent component = new TextComponent(TextComponent.fromLegacyText(PluginUtility.translateMessage(message)));
//        // Add a click event to the component.
//        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
//
//        // Send it!
//        player.spigot().sendMessage(component);
//    }

    default String proceedReplacements(String text, Replacement... replacements) {
        for(Replacement replacement : replacements) {
            if(replacement == null) continue;
            text = replacement.replace(text);
        }
        return text;
    }

    default String getStringValue(Replacement... replacements) {
        Object value = getValue();
        if (!(value instanceof String)) return null;

        String text = value.toString();
        return proceedReplacements(text, replacements);
    }

    default int getInteger() {
        return (int) getValue();
    }

    default boolean getBoolean() {
        return (boolean) getValue();
    }
}
