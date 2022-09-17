package xyz.directplan.candorchannels.channel;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import xyz.directplan.candorchannels.user.User;

/**
 * @author DirectPlan
 */
@CommandAlias("channel")
@CommandPermission("candorchannels.use")
public class ChannelCommand extends BaseCommand {

    @Dependency
    private ChannelManager channelManager;

    @HelpCommand
    @Syntax("")
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }
    
    @CommandAlias("resetchannel")
    @Syntax("")
    public void onResetChannel(User user) {
        channelManager.resetChannel(user);
    }

    @Subcommand("create")
    @Syntax("<name>")
    public void onCreateChannel(User user, String name) {
        channelManager.createChannel(user, name);
    }

    @Subcommand("delete")
    @Syntax("")
    public void deleteChannel(User user) {
        channelManager.deleteChannel(user);
    }

    @Subcommand("switch")
    @Syntax("<channel>")
    public void switchChannel(User user, Channel channel) {
        channelManager.switchChannel(user, channel);
    }
}
