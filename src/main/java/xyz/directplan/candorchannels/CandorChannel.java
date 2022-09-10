package xyz.directplan.candorchannels;

import co.aikar.commands.*;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.directplan.candorchannels.channel.ChannelCommand;
import xyz.directplan.candorchannels.channel.ChannelListener;
import xyz.directplan.candorchannels.channel.ChannelManager;
import xyz.directplan.candorchannels.lib.config.ConfigHandler;
import xyz.directplan.candorchannels.lib.storage.Storage;
import xyz.directplan.candorchannels.user.User;
import xyz.directplan.candorchannels.user.UserListener;
import xyz.directplan.candorchannels.user.UserManager;

@Getter
public final class CandorChannel extends JavaPlugin {

    private Storage storage;
    private ConfigHandler configHandler;

    private UserManager userManager;
    private ChannelManager channelManager;

    @Override
    public void onEnable() {

        configHandler = new BukkitConfigHandler(this);
        configHandler.loadConfiguration("config.yml", ConfigKeys.class);

        storage = new Storage(this);
        storage.connect();

        userManager = new UserManager(this);
        channelManager = new ChannelManager(userManager);

        registerListeners();
        setupCommands();
    }

    public void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new UserListener(userManager), this);
        pluginManager.registerEvents(new ChannelListener(channelManager), this);
    }

    public void setupCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.enableUnstableAPI("help");

        commandManager.registerDependency(ChannelManager.class, channelManager);

        CommandContexts<BukkitCommandExecutionContext> commandContexts = commandManager.getCommandContexts();
        commandContexts.registerIssuerAwareContext(User.class, resolver -> {
            BukkitCommandIssuer commandIssuer = resolver.getIssuer();
            if(resolver.hasFlag("other")) {
                String name = resolver.popFirstArg();
                Player player = ACFBukkitUtil.findPlayerSmart(commandIssuer, name);
                if(player == null) throw new ShowCommandHelp();
                return userManager.getUser(player);
            }
            if(!commandIssuer.isPlayer()) {
                throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE);
            }
            Player player = commandIssuer.getPlayer();
            return userManager.getUser(player);
        });

        // Register channel command
        commandManager.registerCommand(new ChannelCommand());
    }

    @Override
    public void onDisable() {
        configHandler.saveConfigurations();
        userManager.saveAllUsers();
        storage.close();
    }
}
