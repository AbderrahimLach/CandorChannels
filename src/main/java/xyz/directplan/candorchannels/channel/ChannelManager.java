package xyz.directplan.candorchannels.channel;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.directplan.candorchannels.PluginUtility;
import xyz.directplan.candorchannels.user.User;
import xyz.directplan.candorchannels.user.UserManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DirectPlan
 */
@RequiredArgsConstructor
public class ChannelManager {

    private final UserManager userManager;

    private final Map<String, Channel> activeChannels = new HashMap<>();

    public void createChannel(User creator, String name) {
        if(isChannelExist(name)) {
            creator.sendMessage(ChatColor.RED + "This channel already exists.");
            return;
        }
        Channel channel = new Channel(name, creator);
        activeChannels.put(name, channel);
        creator.setCurrentChannel(channel);
        creator.sendMessage(ChatColor.GREEN + "Channel '" + name + "' has been created! ");
    }

    public void switchChannel(User user, Channel channel) {
        if(channel == null) {
            user.sendMessage(ChatColor.RED + "This channel doesn't exist.");
            return;
        }
        Channel currentChannel = user.getCurrentChannel();
        if(currentChannel == channel) {
            user.sendMessage(ChatColor.RED + "You're already in this channel.");
            return;
        }
        if(currentChannel != null) {
            removeAudience(user, currentChannel);
        }
        addAudience(user, channel);
        user.setCurrentChannel(channel);
        user.sendMessage(ChatColor.GREEN + "You've switched to channel '" + channel.getName() + "'");
    }

    public boolean chatChannel(Player player, String message) {
        User user = userManager.getUser(player);
        return chatChannel(user, message);
    }

    public boolean chatChannel(User user, String message) {

        Channel channel = user.getCurrentChannel();
        if(channel == null) {
            return false;
        }
        String channelName = channel.getName();
        for(User audience : channel.getAudiences()) {
            audience.sendMessage(PluginUtility.translateMessage("&7(" + channelName + ") &b" + user.getName() + "&f: " + message));
        }
        return true;
    }

    public void deleteChannel(User user) {
        Channel currentChannel = user.getCurrentChannel();
        if(currentChannel == null) {
            user.sendMessage(ChatColor.RED + "You are not in a channel.");
            return;
        }
        if(!user.isCreator(currentChannel)) {
            user.sendMessage(ChatColor.RED + "You are not the creator of this channel.");
            return;
        }
        deleteChannel(currentChannel);
    }

    public void deleteChannel(Channel channel) {
        List<User> audiences = channel.getAudiences();
        String channelName = channel.getName();
        for(User user : audiences) {
            user.sendMessage(ChatColor.RED + "The channel '"+channelName+"' has been deleted.");
            user.setCurrentChannel(null);
        }
        activeChannels.remove(channelName);
    }

    public void resetChannel(User user) {
        Channel currentChannel = user.getCurrentChannel();
        if(currentChannel == null) {
            user.sendMessage(ChatColor.RED + "You are not in a channel.");
            return;
        }
        removeAudience(user, currentChannel);
        user.setCurrentChannel(null);

        user.sendMessage(ChatColor.GREEN + "You are back to general chat!");
    }


    public void addAudience(User user, Channel channel) {
        channel.addAudience(user);
    }

    public void removeAudience(User user, Channel channel) {
        channel.removeAudience(user);
        List<User> audiences = channel.getAudiences();
        if(audiences.isEmpty()) {
            deleteChannel(channel);
        }
    }

    public Channel getChannel(String name) {
        return activeChannels.get(name);
    }

    public boolean isChannelExist(String name) {
        return getChannel(name) != null;
    }
}
