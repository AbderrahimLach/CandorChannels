package xyz.directplan.candorchannels.channel;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import xyz.directplan.candorchannels.user.User;

/**
 * @author DirectPlan
 */
@CommandAlias("channel")
@CommandPermission("candorchannels.use")
public class ChannelCommand extends BaseCommand {

    @Dependency
    private ChannelManager channelManager;

    @CommandAlias("resetchannel")
    public void onResetChannel(User user) {
        channelManager.resetChannel(user);
    }

    @Subcommand("create")
    public void onCreateChannel(User user, String name) {
        channelManager.createChannel(user, name);
    }

    @Subcommand("delete")
    public void deleteChannel(User user) {
        channelManager.deleteChannel(user);
    }

    @Subcommand("switch")
    public void switchChannel(User user, Channel channel) {
        channelManager.switchChannel(user, channel);
    }
}
