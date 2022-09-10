package xyz.directplan.candorchannels.channel;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author DirectPlan
 */
@RequiredArgsConstructor
public class ChannelListener implements Listener {

    private final ChannelManager channelManager;

    @EventHandler
    public void onChannelChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        boolean success = channelManager.chatChannel(player, message);
        if(success) event.setCancelled(true);
    }
}
