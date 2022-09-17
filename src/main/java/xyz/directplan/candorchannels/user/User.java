package xyz.directplan.candorchannels.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.directplan.candorchannels.channel.Channel;

import java.util.UUID;

/**
 * @author DirectPlan
 */
@Data
@Getter
@Setter
public class User {

    private final UUID uuid;

    private String name;
    private Player player;

    private Channel currentChannel;

    public String getName() {
        if(player != null) return player.getName();
        return name;
    }

    public void sendMessage(String message) {
        if(player == null) return;
        player.sendMessage(message);
    }

    public boolean isCreator(Channel channel) {
        return channel.getCreator() == this;
    }

    public boolean isInChannel() {
        return currentChannel != null;
    }
}
