package xyz.directplan.candorchannels.channel;

import lombok.Data;
import xyz.directplan.candorchannels.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DirectPlan
 */
@Data
public class Channel {

    private final String name;
    private final List<User> audiences;
    private final User creator;

    public Channel(String name, User creator) {
        this.name = name;
        this.audiences = new ArrayList<>();
        this.creator = creator;

        addAudience(creator);
    }

    public void addAudience(User user) {
        audiences.add(user);
    }

    public void removeAudience(User user) {
        audiences.remove(user);
    }
}
